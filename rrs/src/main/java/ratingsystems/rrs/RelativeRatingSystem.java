package ratingsystems.rrs;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.linalg.Matrix;
import ratingsystems.common.linalg.Vector;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.*;

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

    public RelativeRatingSystem(Interpreter interpreter, int[] years) throws FileNotFoundException {
        super(interpreter, years);
        teamNameToIndex = new HashMap<>();
        teamIndexToName = new HashMap<>();
        int i = 0;
        for (String team : teams.keySet()) {
            teamNameToIndex.put(team, i);
            teamIndexToName.put(i, team);
            i++;
        }
    }

    public RelativeRatingSystem(Interpreter interpreter, int[] years, int week) throws FileNotFoundException {
        super(interpreter, years, week);
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

        ratingsystems.common.linalg.Vector posVector = posMatrix.getEigenvector(1.0);
        ratingsystems.common.linalg.Vector negVector = negMatrix.getEigenvector(1.0);

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
            if (addedGroups.add(team.getConference())) {
                groups.put(team.getConference(), new Team(team.getConference()));
                groupSizes.put(team.getConference(), 0);
            }
            Team group = groups.get(team.getConference());
            group.setRating(group.getRating() + team.getRating());
            groupSizes.put(team.getConference(), groupSizes.get(team.getConference()) + 1);
        }
        for (String group : groups.keySet()) {
            groups.get(group).setRating(groups.get(group).getRating() / groupSizes.get(group));
        }
        rankedGroups = new ArrayList<>(groups.values());
        Collections.sort(rankedGroups);
    }

    @Override
    public Prediction predictGame(String team1, String team2) {
        if (!teamNameToIndex.keySet().contains(team1) || !teamNameToIndex.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(teamNameToIndex.get(team1));
        indices.add(teamNameToIndex.get(team2));

        Matrix posMatrix = removeExtraneousConnections(this.posMatrix, indices);//this.posMatrix.copy();
        Matrix negMatrix = removeExtraneousConnections(this.negMatrix, indices);//this.negMatrix.copy();

        double[] input = new double[teams.size()];
        for (int i = 0; i < input.length; i++) {
            input[i] = 0;
        }
        for (int i : indices) {
            input[i] = 0.5;
        }
        ratingsystems.common.linalg.Vector inputVector = new ratingsystems.common.linalg.Vector(input);

        ratingsystems.common.linalg.Vector posVector = inputVector.copy();
        ratingsystems.common.linalg.Vector negVector = inputVector.copy();
        for (int i = 0; i < 1; i++) {
            posVector = posMatrix.multiply(posVector);
            negVector = negMatrix.multiply(negVector);
        }

        double team1PosScore = posVector.get(indices.get(0));
        double team1NegScore = negVector.get(indices.get(0));
        double team2PosScore = posVector.get(indices.get(1));
        double team2NegScore = negVector.get(indices.get(1));

        double posDiff = team1PosScore - team2PosScore;
        double negDiff = team2NegScore - team1NegScore;
        double totalDiff = posDiff + negDiff;

        double a = 0.9;
        double b = 1.8;
        double odds = a * Math.tan(Math.atan(0.5 / a) * Math.tanh(b * totalDiff)) + 0.5;

        return new Prediction(team1, team2, odds);
    }

    @Override
    protected String printTeam(String team) {
        return teams.get(team).getName() + "\t"
                + (int)teams.get(team).getRating() + "\t"
                + teams.get(team).getRecord();
    }

    @Override
    protected String prettyPrintTeam(String team) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + Terminal.rightJustify(Integer.toString((int)teams.get(team).getRating()), 10) + "   "
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
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
                if (game.getScoreDiff() > 0) {
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] += Math.abs(game.getWeightedScoreDiff());
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
                if (game.getScoreDiff() < 0) {
                    if (!teamNameToIndex.containsKey(game.getOpponent())) {
                        System.out.println(game.getOpponent());
                    }
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] += Math.abs(game.getWeightedScoreDiff());
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
            if (colSum > 0.0) {
                for (int r = 0; r < values.length; r++) {
                    values[r][c] /= colSum;
                }
            }
        }
        return new Matrix(values);
    }

    /**
     * Sets the rating that comes from the positive value matrix for every team
     *
     * @param vector the eigenvector corresponding to the positive ratings
     */
    private void setPositiveRatings(ratingsystems.common.linalg.Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            teams.get(teamIndexToName.get(i)).setRating("Positive Rating", (int)(vector.get(i) * 10000));
        }
    }

    /**
     * Sets the rating that comes from the negative value matrix for every team
     *
     * @param vector the eigenvector corresponding to the negative ratings
     */
    private void setNegativeRatings(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            teams.get(teamIndexToName.get(i)).setRating("Negative Rating", (int)(vector.get(i) * 10000));
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

    private Matrix removeExtraneousConnections(Matrix matrix, List<Integer> indices) {
        Matrix matrixCopy = matrix.copy();
        for (int c = 0; c < matrixCopy.columns(); c++) {
            if (!indices.contains(c)) {
                double value = matrixCopy.get(c, c);
                double count = 0.0;
                matrixCopy.set(c, c, 0.0);
                for (int r = 0; r < matrixCopy.rows(); r++) {
                    if (matrixCopy.get(r, c) > 0) {
                        count++;
                    }
                }
                value /= count;
                for (int r = 0; r < matrixCopy.rows(); r++) {
                    if (matrixCopy.get(r, c) > 0) {
                        matrixCopy.set(r, c, matrixCopy.get(r, c) + value);
                    }
                }
            }
        }
        return matrixCopy;
    }
}
