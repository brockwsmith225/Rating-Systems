package ratingsystems.hps;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.common.linalg.Vector;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoricalPredictionSystem extends RatingSystem {

    private HashMap<Integer, HashMap<String, Vector>> teamVectors;
    private HashMap<Integer, HashMap<String, Vector>> scaledTeamVectors;
    private HashMap<Integer, HashMap<String, Team>> allTeams;
    private int year;

    public HistoricalPredictionSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        this.year = year;
        teamVectors = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        teamVectors.put(year, new HashMap<>());
        for (String team : teams.keySet()) {
            allTeams.get(year).put(team, teams.get(team));
            teamVectors.get(year).put(team, teams.get(team).getStatisticsVector());
        }
        for (int y = 2014; y < year; y++) {
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
        this.year = year;
        teamVectors = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        teamVectors.put(year, new HashMap<>());
        for (String team : teams.keySet()) {
            allTeams.get(year).put(team, teams.get(team));
            teamVectors.get(year).put(team, teams.get(team).getStatisticsVector());
        }
        for (int y = 2014; y < year; y++) {
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

//        HashMap<String, Double> ratings = new HashMap<>();
//        for (String team : allTeams.get(this.year).keySet()) {
//            ratings.put(team, 0.0);
//            for (String opponent : allTeams.get(this.year).keySet()) {
//                if (!team.equals(opponent)) {
//                    ratings.put(team, ratings.get(team) + predictGame(team, opponent).getLine());
//                }
//            }
//        }
//
//        for (String team : allTeams.get(year).keySet()) {
//            allTeams.get(this.year).get(team).setRating(ratings.get(team));
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
        ArrayList<Double> gameResults = new ArrayList<>();
        double wins = 0.0;
        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    for (Game game : allTeams.get(year).get(historicalTeam).getGames()) {
                        double gameSimilarity = Math.pow(team1Similarities.get(historicalTeam) * team2Similarities.get(game.getOpponent()), this.year - year);
                        gameSimilarities.add(gameSimilarity);
                        gameResults.add(game.getWeightedScoreDiff() * (game.getScoreDiff() / Math.abs(game.getScoreDiff())));
                        if (game.getScoreDiff() > 0) {
                            wins++;
                        }
                    }
                }
            }
        }

        Vector gameSimilaritiesVector = new Vector(gameSimilarities).softmax();
        Vector gameResultsVector = new Vector(gameResults);

        double expectedResult = gameSimilaritiesVector.dotProduct(gameResultsVector);

        return new Prediction(team1, team2, 0.5 + expectedResult, expectedResult);
    }

}
