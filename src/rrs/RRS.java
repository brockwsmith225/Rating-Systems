package rrs;

import interpreter.Interpreter;
import interpreter.datatypes.DataPoint;
import ratingSystem.RatingSystem;
import rrs.datatypes.Matrix;
import rrs.datatypes.Vector;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RRS extends RatingSystem {

    private HashMap<String, Integer> entityNameToIndex;
    private HashMap<Integer, String> entityIndexToName;

    public RRS() {
        super();
    }

    public RRS(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        entityNameToIndex = new HashMap<>();
        entityIndexToName = new HashMap<>();
        int i = 0;
        for (String entity : entities.keySet()) {
            entityNameToIndex.put(entity, i);
            entityIndexToName.put(i, entity);
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
        Matrix negIdentity = Matrix.generateIdentityMatrix(entities.size()).multiply(-1.0);

        posMatrix = posMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));
        negMatrix = negMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));

        Vector posVector = posMatrix.getEigenvector(1.0);
        Vector negVector = negMatrix.getEigenvector(1.0);

        setPositiveRatings(posVector);
        setNegativeRatings(negVector);
        setRatings();

        rankEntities();
    }

    @Override
    public void rankEntities() {
        rankedEntities = new ArrayList<>(entities.values());
        Collections.sort(rankedEntities);
    }

    @Override
    public void rankGroups() {}



    //========== RRS only methods ==========

    /**
     * Sets up a 2D array with the values from all data points which are positive for the entity associated with it
     *
     * @return the 2D array with all positive values
     */
    private double[][] setupPositiveValues() {
        double[][] values = new double[entities.size()][entities.size()];
        for (int r = 0; r < entities.size(); r++) {
            for (int c = 0; c < entities.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String entity : entities.keySet()) {
            ArrayList<DataPoint> dataPoints = entities.get(entity).getDataPoints();
            double totalWeightedScoreDiff = 0.0;
            for (DataPoint dataPoint : dataPoints) {
                if (dataPoint.getWeightedScoreDiff() > 0) {
                    values[entityNameToIndex.get(entity)][entityNameToIndex.get(dataPoint.getOtherEntity())] = Math.abs(dataPoint.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(dataPoint.getWeightedScoreDiff());
                }
            }
            values[entityNameToIndex.get(entity)][entityNameToIndex.get(entity)] = totalWeightedScoreDiff;
        }
        return values;
    }

    /**
     * Sets up a 2D array with the values from all data points which are negative for the entity associated with it
     *
     * @return the 2D array with all negative values
     */
    private double[][] setupNegativeValues() {
        double[][] values = new double[entities.size()][entities.size()];
        for (int r = 0; r < entities.size(); r++) {
            for (int c = 0; c < entities.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String entity : entities.keySet()) {
            ArrayList<DataPoint> dataPoints = entities.get(entity).getDataPoints();
            double totalWeightedScoreDiff = 0.0;
            for (DataPoint dataPoint : dataPoints) {
                if (dataPoint.getWeightedScoreDiff() < 0) {
                    values[entityNameToIndex.get(entity)][entityNameToIndex.get(dataPoint.getOtherEntity())] = Math.abs(dataPoint.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(dataPoint.getWeightedScoreDiff());
                }
            }
            values[entityNameToIndex.get(entity)][entityNameToIndex.get(entity)] = totalWeightedScoreDiff;
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
        double[][] partials = new double[entities.size()][entities.size()];
        for (int r = 0; r < entities.size(); r++) {
            for (int c = 0; c < entities.size(); c++) {
                partials[r][c] = 1.0 / entities.size();
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
            entities.get(entityIndexToName.get(i)).setRating("Positive Rating", vector.get(i));
        }
    }

    /**
     * Sets the rating that comes from the negative value matrix for every entity
     *
     * @param vector the eigenvector corresponding to the negative ratings
     */
    private void setNegativeRatings(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            entities.get(entityIndexToName.get(i)).setRating("Negative Rating", vector.get(i));
        }
    }

    /**
     * Sets the ratings for every entity based on its positive and negative ratings
     */
    private void setRatings() {
        for (String entity : entities.keySet()) {
            entities.get(entity).setRating(entities.get(entity).getRating("Positive Rating") - entities.get(entity).getRating("Negative Rating"));
        }
    }
}
