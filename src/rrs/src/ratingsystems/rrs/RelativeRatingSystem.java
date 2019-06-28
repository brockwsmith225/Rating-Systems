package ratingsystems.rrs;

import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class RelativeRatingSystem extends RatingSystem {

    private Matrix posMatrix, negMatrix;
    private HashMap<String, Integer> teamNameToIndex;
    private HashMap<Integer, String> teamIndexToName;

    public RelativeRatingSystem() {
        super();
    }

    public RelativeRatingSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        teamNameToIndex = new HashMap<>();
        teamIndexToName = new HashMap<>();
        int i = 0;
        for (String team : teams.keySet()) {
            teamNameToIndex.put(team, i);
            teamIndexToName.put(i, team);
            i++;
        }
    }

    public RelativeRatingSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        teamNameToIndex = new HashMap<>();
        teamIndexToName = new HashMap<>();
        int i = 0;
        for (String team : teams.keySet()) {
            teamNameToIndex.put(team, i);
            teamIndexToName.put(i, team);
            i++;
        }
    }

    @Override
    public void setup() {
        double[][] posValues = setupPositiveValues();
        double[][] negValues = setupNegativeValues();
        double[][] partials = setupPartials();

        this.posMatrix = convertToProbabiltyMatrix(posValues);
        this.negMatrix = convertToProbabiltyMatrix(negValues);
        Matrix partialsMatrix = new Matrix(partials);

        Matrix posMatrix = this.posMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));
        Matrix negMatrix = this.negMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));

        Vector posVector = posMatrix.getEigenvector(1.0);
        Vector negVector = negMatrix.getEigenvector(1.0);

        setPositiveRatings(posVector);
        setNegativeRatings(negVector);
        setRatings();

        rankTeams();
        rankGroups();
    }

    @Override
    public void rankGroups() {
        HashSet<String> addedGroups = new HashSet<>();
        HashMap<String, Team> groups = new HashMap<>();
        HashMap<String, Integer> groupSizes = new HashMap<>();
        for (Team team : rankedTeams) {
            if (addedGroups.add(team.getGroup())) {
                groups.put(team.getGroup(), new Team(team.getGroup()));
                groupSizes.put(team.getGroup(), 0);
            }
            Team group = groups.get(team.getGroup());
            group.setRating(group.getRating() + team.getRating());
            groupSizes.put(team.getGroup(), groupSizes.get(team.getGroup()) + 1);
        }
        for (String group : groups.keySet()) {
            groups.get(group).setRating(groups.get(group).getRating() / groupSizes.get(group));
        }
        rankedGroups = new ArrayList<>(groups.values());
        Collections.sort(rankedGroups);
    }

    @Override
    public double predictGame(String team1, String team2) {
        return 0.0;
    }



    //========== RelativeRatingSystem only methods ==========

    /**
     * Sets up a 2D array with the values from all games which are positive for the team associated with it
     *
     * @return the 2D array with all positive values
     */
    private double[][] setupPositiveValues() {
        double[][] values = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String team : teams.keySet()) {
            ArrayList<Game> games = teams.get(team).getGames();
            double totalWeightedScoreDiff = 0.0;
            for (Game game : games) {
                if (game.getWeightedScoreDiff() > 0) {
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] = Math.abs(game.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(game.getWeightedScoreDiff());
                }
            }
            values[teamNameToIndex.get(team)][teamNameToIndex.get(team)] = totalWeightedScoreDiff;
        }
        return values;
    }

    /**
     * Sets up a 2D array with the values from all games which are negative for the team associated with it
     *
     * @return the 2D array with all negative values
     */
    private double[][] setupNegativeValues() {
        double[][] values = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String team : teams.keySet()) {
            ArrayList<Game> games = teams.get(team).getGames();
            double totalWeightedScoreDiff = 0.0;
            for (Game game : games) {
                if (game.getWeightedScoreDiff() < 0) {
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] = Math.abs(game.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(game.getWeightedScoreDiff());
                }
            }
            values[teamNameToIndex.get(team)][teamNameToIndex.get(team)] = totalWeightedScoreDiff;
        }
        return values;
    }

    /**
     * Sets up a 2D array with the partial values necessary for guaranteeing that the matrices will be regular
     * stochastic matrices (which is necessary for the row reduction step)
     *
     * @return the 2D array with the partial values
     */
    private double[][] setupPartials() {
        double[][] partials = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                partials[r][c] = 1.0 / teams.size();
            }
        }
        return partials;
    }

    /**
     * Convertes a 2D array of values to a probability matrix
     *
     * @param values the 2D array of values to be converted
     * @return a probability matrix made from the inputted values
     */
    private Matrix convertToProbabiltyMatrix(double[][] values) {
        for (int c = 0; c < values[0].length; c++) {
            double colSum = 0.0;
            for (int r = 0; r < values.length; r++) {
                colSum += values[r][c];
            }
            for (int r = 0; r < values.length; r++) {
                values[r][c] /= colSum;
            }
        }
        return new Matrix(values);
    }

    /**
     * Sets the rating that comes from the positive value matrix for every team
     *
     * @param vector the eigenvector corresponding to the positive ratings
     */
    private void setPositiveRatings(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            teams.get(teamIndexToName.get(i)).setRating("Positive Rating", vector.get(i));
        }
    }

    /**
     * Sets the rating that comes from the negative value matrix for every team
     *
     * @param vector the eigenvector corresponding to the negative ratings
     */
    private void setNegativeRatings(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            teams.get(teamIndexToName.get(i)).setRating("Negative Rating", vector.get(i));
        }
    }

    /**
     * Sets the ratings for every team based on its positive and negative ratings
     */
    private void setRatings() {
        for (String team : teams.keySet()) {
            teams.get(team).setRating(teams.get(team).getRating("Positive Rating") - teams.get(team).getRating("Negative Rating"));
        }
    }
}
