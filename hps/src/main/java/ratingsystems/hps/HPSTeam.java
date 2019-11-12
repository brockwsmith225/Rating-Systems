package ratingsystems.hps;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.linalg.Vector;
import ratingsystems.ser.SERTeam;

import java.util.HashMap;
import java.util.Map;

public class HPSTeam extends SERTeam {

    Map<String, Double> avgStats;
    Map<String, Double> scaledStats;

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
                if (scaledStats.containsKey(stat)) {
                    scaledStats.put(stat, scaledStats.get(stat) + game.getStatistic(stat) / teams.get(game.getOpponent()).avgStats.get(stat));
                } else {
                    scaledStats.put(stat, game.getStatistic(stat) / teams.get(game.getOpponent()).avgStats.get(stat));
                }
            }
        }
        scaledStats.replaceAll((key, value) -> {
            double newValue = value / team.getNumberOfGames();
            if (Double.isNaN(newValue)) {
                return 1.0;
            }
            if (Double.isInfinite(newValue) || newValue > 10) {
                return 10.0;
            }
            if (newValue < 0.1) {
                return 0.1;
            }
            return newValue;
        });
    }

    public double similarity(HPSTeam team) {
//        Vector vector1 = new Vector(scaledStats.values());
//        Vector vector2 = new Vector(team.scaledStats.values());
//        double similarity = vector1.cosineSimilarity(vector2);
//        if (Double.isNaN(similarity)) {
//            return 0.0;
//        } else if (Double.isInfinite(similarity)) {
//            return 1.0;
//        }3
//        return similarity;
        double similarity = 0.0;
        for (String stat : scaledStats.keySet()) {
            double statSim = scaledStats.get(stat) / team.scaledStats.get(stat);
            similarity += statSim <= 1 ? statSim : 1.0 / statSim;
        }
        return similarity / scaledStats.size();
    }

}
