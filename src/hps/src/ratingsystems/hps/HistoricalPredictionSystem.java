package ratingsystems.hps;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.common.linalg.Vector;
import ratingsystems.ser.SimpleEfficiencyRating;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoricalPredictionSystem extends RatingSystem {

    private HashMap<Integer, HashMap<String, Vector>> teamVectors;
    private HashMap<Integer, HashMap<String, Vector>> scaledTeamVectors;
    private HashMap<Integer, HashMap<String, Team>> allTeams;
    private SimpleEfficiencyRating ser;

    public HistoricalPredictionSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        teamVectors = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        teamVectors.put(year, new HashMap<>());
        for (String team : teams.keySet()) {
            allTeams.get(year).put(team, teams.get(team));
            teamVectors.get(year).put(team, teams.get(team).getStatisticsVector());
        }
        for (int y = 2012; y < year; y++) {
            if (interpreter.hasData(y)) {
                HashMap<String, Team> temp = interpreter.parseData(y);
                allTeams.put(y, new HashMap<>());
                teamVectors.put(y, new HashMap<>());
                for (String team : temp.keySet()) {
                    allTeams.get(y).put(team, temp.get(team));
                    teamVectors.get(y).put(team, temp.get(team).getStatisticsVector());
                }
            }
        }
    }

    public HistoricalPredictionSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        teamVectors = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        teamVectors.put(year, new HashMap<>());
        for (String team : teams.keySet()) {
            allTeams.get(year).put(team, teams.get(team));
            teamVectors.get(year).put(team, teams.get(team).getStatisticsVector());
        }
        for (int y = 2012; y < year; y++) {
            if (interpreter.hasData(y)) {
                HashMap<String, Team> temp = interpreter.parseData(y);
                allTeams.put(y, new HashMap<>());
                teamVectors.put(y, new HashMap<>());
                for (String team : temp.keySet()) {
                    allTeams.get(y).put(team, temp.get(team));
                    teamVectors.get(y).put(team, temp.get(team).getStatisticsVector());
                }
            }
        }
    }

    @Override
    public void setup() {
        scaledTeamVectors = new HashMap<>();
        for (Integer year : allTeams.keySet()) {
            scaledTeamVectors.put(year, new HashMap<>());
            for (String team : allTeams.get(year).keySet()) {
                scaledTeamVectors.get(year).put(team, allTeams.get(year).get(team).getStatisticsVector(teamVectors.get(year)));
            }
        }

        try {
            if (this.week < 0) {
                this.ser = new SimpleEfficiencyRating(this.interpreter, this.year);
            } else {
                this.ser = new SimpleEfficiencyRating(this.interpreter, this.year, this.week);
            }
            this.ser.setup();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        HashMap<String, Double> ratings = new HashMap<>();
//        for (String team : allTeams.get(this.year).keySet()) {
//            ratings.put(team, 0.0);
//            for (String opponent : allTeams.get(this.year).keySet()) {
//                if (!team.equals(opponent)) {
//                    ratings.put(team, ratings.get(team) + predictGame(team, opponent).getLine() * -1);
//                }
//            }
//        }
//
//        for (String team : allTeams.get(year).keySet()) {
//            allTeams.get(this.year).get(team).setRating(ratings.get(team) / (ratings.size() - 1));
//        }

        rankTeams();
        rankGroups();
    }

    @Override
    public void rankGroups() {

    }

    @Override
    public Prediction predictGame(String team1, String team2) {
        if (!teams.keySet().contains(team1) || !teams.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        HashMap<String, Double> team1Similarities = new HashMap<>();
        HashMap<String, Double> team2Similarities = new HashMap<>();

        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    team1Similarities.put(historicalTeam, scaledTeamVectors.get(this.year).get(team1).cosineSimilarity(scaledTeamVectors.get(year).get(historicalTeam)));
                    team2Similarities.put(historicalTeam, scaledTeamVectors.get(this.year).get(team2).cosineSimilarity(scaledTeamVectors.get(year).get(historicalTeam)));
                }
            }
        }

        ArrayList<Double> gameSimilarities = new ArrayList<>();
        ArrayList<Double> team1Scores = new ArrayList<>();
        ArrayList<Double> team2Scores = new ArrayList<>();
        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    for (Game game : allTeams.get(year).get(historicalTeam).getGames()) {
                        double gameSimilarity = team1Similarities.get(historicalTeam) * team2Similarities.get(game.getOpponent());
                        gameSimilarities.add(gameSimilarity);
                        team1Scores.add(game.getScore());
                        team2Scores.add(game.getOpponentScore());
                    }
                }
            }
        }

        Vector gameSimilaritiesVector = new Vector(gameSimilarities).softmax();
        Vector team1ScoresVector = new Vector(team1Scores);
        Vector team2ScoresVector = new Vector(team2Scores);

        Team team1SER = this.ser.getTeam(team1);
        Team team2SER = this.ser.getTeam(team2);
        double team1ExpectedScore = gameSimilaritiesVector.dotProduct(team1ScoresVector) * team1SER.getRating("Offensive Rating") / team2SER.getRating("Defensive Rating");
        double team2ExpectedScore = gameSimilaritiesVector.dotProduct(team2ScoresVector) * team2SER.getRating("Offensive Rating") / team1SER.getRating("Defensive Rating");

        double odds = team1ExpectedScore / (team1ExpectedScore + team2ExpectedScore);

        return new Prediction(team1, team2, odds, team1ExpectedScore, team2ExpectedScore);
    }

}
