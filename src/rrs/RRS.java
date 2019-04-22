package rrs;

import interpreter.datatypes.DataPoint;
import ratingSystem.RatingSystem;
import rrs.datatypes.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RRS extends RatingSystem {

    private HashMap<String, Integer> entityNameToIndex;
    private HashMap<Integer, String> entityIndexToName;

    @Override
    public void setup() {
        double[][] posValues = setupPositiveValues();
        double[][] negValues = setupNegativeValues();
        double[][] partials = setupPartials();

        Matrix posMatrix = convertToProbabiltyMatrix(posValues);
        Matrix negMatrix = convertToProbabiltyMatrix(negValues);
        Matrix partialsMatrix = new Matrix(partials);
        Matrix negIdentity = Matrix.generateIdentityMatrix(entities.size()).multiply(-1.0);

        posMatrix = posMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1)).add(negIdentity).rowReduce();
        negMatrix = negMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1)).add(negIdentity).rowReduce();

        setPositiveRatings(posMatrix);
        setNegativeRatings(negMatrix);
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

    private double[][] setupPartials() {
        double[][] partials = new double[entities.size()][entities.size()];
        for (int r = 0; r < entities.size(); r++) {
            for (int c = 0; c < entities.size(); c++) {
                partials[r][c] = 1.0 / entities.size();
            }
        }
        return partials;
    }

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

    private void setPositiveRatings(Matrix matrix) {
        double ratingSum = 0.0;
        for (int i = 0; i < matrix.rows(); i++) {
            ratingSum += matrix.get(i, i);
        }
        for (int i = 0; i < matrix.rows(); i++) {
            entities.get(entityIndexToName.get(i)).setRating("Positive Rating", matrix.get(i, i) / ratingSum);
        }
    }

    private void setNegativeRatings(Matrix matrix) {
        double ratingSum = 0.0;
        for (int i = 0; i < matrix.rows(); i++) {
            ratingSum += matrix.get(i, i);
        }
        for (int i = 0; i < matrix.rows(); i++) {
            entities.get(entityIndexToName.get(i)).setRating("Negative Rating", matrix.get(i, i) / ratingSum);
        }
    }

    private void setRatings() {
        for (String entity : entities.keySet()) {
            entities.get(entity).setRating(entities.get(entity).getRating("Positive Rating") - entities.get(entity).getRating("Negative Rating"));
        }
    }
}
