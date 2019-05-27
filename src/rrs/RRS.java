package rrs;

import interpreter.Interpreter;
import interpreter.datatypes.DataPoint;
import interpreter.datatypes.Team;
import ratingSystem.RatingSystem;
import rrs.datatypes.Matrix;
import rrs.datatypes.Vector;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class RRS extends RatingSystem {

    private HashMap<String, Integer> teamNameToIndex;
    private HashMap<Integer, String> teamIndexToName;

    public RRS() {
        super();
    }

    public RRS(Interpreter interpreter, int year) throws FileNotFoundException {
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

    @Override
    public void setup() {
        double[][] posValues = setupPositiveValues();
        double[][] negValues = setupNegativeValues();
        double[][] partials = setupPartials();

        Matrix posMatrix = convertToProbabiltyMatrix(posValues);
        Matrix negMatrix = convertToProbabiltyMatrix(negValues);
        Matrix partialsMatrix = new Matrix(partials);

        posMatrix = posMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));
        negMatrix = negMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));

        Vector posVector = posMatrix.getEigenvector(1.0);
        Vector negVector = negMatrix.getEigenvector(1.0);

        setPositiveRatings(posVector);
        setNegativeRatings(negVector);
        setRatings();

        rankTeams();
        rankGroups();
    }

    @Override
    public void rankTeams() {
        rankedTeams = new ArrayList<>(teams.values());
        Collections.sort(rankedTeams);
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



    //========== RRS only methods ==========

    /**
     * Sets up a 2D array with the values from all data points which are positive for the entity associated with it
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
        for (String entity : teams.keySet()) {
            ArrayList<DataPoint> dataPoints = teams.get(entity).getDataPoints();
            double totalWeightedScoreDiff = 0.0;
            for (DataPoint dataPoint : dataPoints) {
                if (dataPoint.getWeightedScoreDiff() > 0) {
                    values[teamNameToIndex.get(entity)][teamNameToIndex.get(dataPoint.getOtherEntity())] = Math.abs(dataPoint.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(dataPoint.getWeightedScoreDiff());
                }
            }
            values[teamNameToIndex.get(entity)][teamNameToIndex.get(entity)] = totalWeightedScoreDiff;
        }
        return values;
    }

    /**
     * Sets up a 2D array with the values from all data points which are negative for the entity associated with it
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
        for (String entity : teams.keySet()) {
            ArrayList<DataPoint> dataPoints = teams.get(entity).getDataPoints();
            double totalWeightedScoreDiff = 0.0;
            for (DataPoint dataPoint : dataPoints) {
                if (dataPoint.getWeightedScoreDiff() < 0) {
                    values[teamNameToIndex.get(entity)][teamNameToIndex.get(dataPoint.getOtherEntity())] = Math.abs(dataPoint.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(dataPoint.getWeightedScoreDiff());
                }
            }
            values[teamNameToIndex.get(entity)][teamNameToIndex.get(entity)] = totalWeightedScoreDiff;
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
     * Sets the rating that comes from the positive value matrix for every entity
     *
     * @param vector the eigenvector corresponding to the positive ratings
     */
    private void setPositiveRatings(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            teams.get(teamIndexToName.get(i)).setRating("Positive Rating", vector.get(i));
        }
    }

    /**
     * Sets the rating that comes from the negative value matrix for every entity
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
        for (String entity : teams.keySet()) {
            teams.get(entity).setRating(teams.get(entity).getRating("Positive Rating") - teams.get(entity).getRating("Negative Rating"));
        }
    }
}
