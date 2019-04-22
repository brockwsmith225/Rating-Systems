package rrs;

import interpreter.datatypes.DataPoint;
import ratingSystem.RatingSystem;
import rrs.datatypes.Matrix;

import java.util.ArrayList;
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
    }

    @Override
    public void rankEntities() {}

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
}
