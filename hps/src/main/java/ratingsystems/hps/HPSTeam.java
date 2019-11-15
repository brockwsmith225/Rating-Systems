package ratingsystems.hps;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.linalg.Vector;
import ratingsystems.ser.SERTeam;

import java.util.HashMap;
import java.util.Map;

public class HPSTeam extends SERTeam {

    private Map<String, Double> avgStats;
    private Map<String, Double> scaledStats;

    public HPSTeam(Team team) {
        super(team);
        avgStats = new HashMap<>();
        for (Game game : team.getGames()) {
            for (String stat : game.getStatistics().keySet()) {
                if (avgStats.containsKey(stat)) {
                    avgStats.put(stat, avgStats.get(stat) + game.getStatistic(stat));
                } else {
                    avgStats.put(stat, game.getStatistic(stat));
                }
            }
        }
        avgStats.replaceAll((key, value) -> value / team.getNumberOfGames());
    }

    public void setScaledStats(Map<String, HPSTeam> teams) {
        scaledStats = new HashMap<>();
        for (Game game : team.getGames()) {
            for (String stat : game.getStatistics().keySet()) {
                double scaledStat = Math.exp(game.getStatistic(stat) / teams.get(game.getOpponent()).avgStats.get(stat) - 1);
                if (Double.isNaN(scaledStat) || Double.isInfinite(scaledStat)) scaledStat = 1.0;
                if (scaledStats.containsKey(stat)) {
                    scaledStats.put(stat, scaledStats.get(stat) * scaledStat);
                } else {
                    scaledStats.put(stat, scaledStat);
                }
            }
        }
        scaledStats.replaceAll((key, value) -> Math.pow(value, 1.0 / team.getNumberOfGames()));
    }

    public double offensePassingPlayPercent() {
        return avgStats.get("PassAttempts") / (avgStats.get("PassAttempts") + avgStats.get("RushAttempts"));
    }

    public double offenseRushingPlayPercent() {
        return avgStats.get("RushAttempts") / (avgStats.get("PassAttempts") + avgStats.get("RushAttempts"));
    }

    public double offensePassingEfficiency() {
        double scoreEff = scaledStats.get("PassTDs");
        double yardEff = scaledStats.get("PassYards");
        double compEff = scaledStats.get("PassCompletions") / scaledStats.get("PassAttempts");
        return Math.pow(scoreEff * yardEff * compEff, 1.0 / 3.0);
    }

    public double offenseRushingEfficiency() {
        double scoreEff = scaledStats.get("RushTDs");
        double yardEff = scaledStats.get("RushYards");
        return Math.pow(scoreEff * yardEff, 1.0 / 2.0);
    }

    public double offenseEfficiency() {
        double offPassEff = offensePassingEfficiency();
        double offRushEff = offenseRushingEfficiency();
        return Math.pow(offPassEff, offensePassingPlayPercent()) * Math.pow(offRushEff, offenseRushingPlayPercent());
    }

    public double defensePassingPlayPercent() {
        return avgStats.get("OpponentPassAttempts") / (avgStats.get("OpponentPassAttempts") + avgStats.get("OpponentRushAttempts"));
    }

    public double defenseRushingPlayPercent() {
        return avgStats.get("OpponentRushAttempts") / (avgStats.get("OpponentPassAttempts") + avgStats.get("OpponentRushAttempts"));
    }

    public double defensePassingEfficiency() {
        double scoreEff = scaledStats.get("OpponentPassTDs");
        double yardEff = scaledStats.get("OpponentPassYards");
        double compEff = scaledStats.get("OpponentPassCompletions") / scaledStats.get("OpponentPassAttempts");
        return 1.0 / Math.pow(scoreEff * yardEff * compEff, 1.0 / 3.0);
    }

    public double defenseRushingEfficiency() {
        double scoreEff = scaledStats.get("OpponentRushTDs");
        double yardEff = scaledStats.get("OpponentRushYards");
        return 1.0 / Math.pow(scoreEff * yardEff, 1.0 / 2.0);
    }

    public double defenseEfficiency() {
        double defPassEff = defensePassingEfficiency();
        double defRushEff = defenseRushingEfficiency();
        return Math.pow(defPassEff, defensePassingPlayPercent()) * Math.pow(defRushEff, defenseRushingPlayPercent());
    }

    public double similarity(HPSTeam team) {
//        Vector vector1 = new Vector(scaledStats.values());
//        Vector vector2 = new Vector(team.scaledStats.values());
//        double similarity = vector1.cosineSimilarity(vector2);
//        if (Double.isNaN(similarity)) {
//            return 0.0;
//        } else if (Double.isInfinite(similarity)) {
//            return 1.0;
//        }
//        return similarity;
//        double similarity = 1.0;
//        for (String stat : scaledStats.keySet()) {
//            double statSim = scaledStats.get(stat) / team.scaledStats.get(stat);
//            similarity *= statSim <= 1 ? statSim : 1.0 / statSim;
//        }
//        return Math.pow(similarity, 1.0 / scaledStats.size());
        double similarity = 1.0;
        similarity *= multAbs(offensePassingEfficiency() / team.offensePassingEfficiency());
        similarity *= multAbs(offenseRushingEfficiency() / team.offenseRushingEfficiency());
        similarity *= multAbs(offensePassingPlayPercent() / team.offensePassingPlayPercent());
        similarity *= multAbs(defensePassingEfficiency() / team.defensePassingEfficiency());
        similarity *= multAbs(defenseRushingEfficiency() / team.defenseRushingEfficiency());
        similarity *= multAbs(defensePassingPlayPercent() / team.defensePassingPlayPercent());
        return Math.pow(similarity, 1.0 / 6.0);
    }

    private double multAbs(double x) {
        return x <= 1 ? x : 1.0 / x;
    }

}
