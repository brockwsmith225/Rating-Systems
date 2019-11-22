package ratingsystems.rhps;

import ratingsystems.common.commands.Predict;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.hps.HistoricalPredictionSystem;
import ratingsystems.rrs.RelativeRatingSystem;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelativeHistoricalPredictionSystem extends RelativeRatingSystem {

    RatingSystem hps;

    public RelativeHistoricalPredictionSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        hps = new HistoricalPredictionSystem(interpreter, year);
    }

    public RelativeHistoricalPredictionSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        hps = new HistoricalPredictionSystem(interpreter, year, week);
    }

    @Override
    public void setup() {
        hps.setup();
        super.setup();
    }

    @Override
    protected double[][] setupPositiveValues() {
        double[][] values = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String team : teams.keySet()) {
            List<Game> games = teams.get(team).getGames();
            double totalWeightedScoreDiff = 0.0;
            Set<String> addedTeams = new HashSet<>();
            for (Game game : games) {
                if (game.getScoreDiff() > 0) {
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] += Math.abs(2 * game.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(2 * game.getWeightedScoreDiff());
                    addedTeams.add(game.getOpponent());
                }
            }
            for (String opponent : teams.keySet()) {
                if (addedTeams.add(opponent)) {
                    Prediction prediction = hps.predictGame(team, opponent, Location.NEUTRAL);
                    if (prediction.getOdds() > 0.5) {
                        values[teamNameToIndex.get(team)][teamNameToIndex.get(opponent)] += Math.abs(prediction.getLine()) + 10;
                        totalWeightedScoreDiff += Math.abs(prediction.getLine()) + 10;
                    }
                }
            }
            values[teamNameToIndex.get(team)][teamNameToIndex.get(team)] = totalWeightedScoreDiff;
        }
        return values;
    }

    @Override
    protected double[][] setupNegativeValues() {
        double[][] values = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String team : teams.keySet()) {
            List<Game> games = teams.get(team).getGames();
            double totalWeightedScoreDiff = 0.0;
            Set<String> addedTeams = new HashSet<>();
            for (Game game : games) {
                if (game.getScoreDiff() < 0) {
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] += Math.abs(2 * game.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(2 * game.getWeightedScoreDiff());
                    addedTeams.add(game.getOpponent());
                }
            }
            for (String opponent : teams.keySet()) {
                if (addedTeams.add(opponent)) {
                    Prediction prediction = hps.predictGame(team, opponent, Location.NEUTRAL);
                    if (prediction.getOdds() < 0.5) {
                        values[teamNameToIndex.get(team)][teamNameToIndex.get(opponent)] += Math.abs(prediction.getLine()) + 10;
                        totalWeightedScoreDiff += Math.abs(prediction.getLine()) + 10;
                    }
                }
            }
            values[teamNameToIndex.get(team)][teamNameToIndex.get(team)] = totalWeightedScoreDiff;
        }
        return values;
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        return hps.predictGame(team1, team2, location);
    }

}
