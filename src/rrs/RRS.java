package rrs;

import interpreter.datatypes.DataPoint;
import ratingSystem.RatingSystem;

import java.util.ArrayList;
import java.util.HashMap;

public class RRS extends RatingSystem {

    private HashMap<String, Integer> entityNameToIndex;
    private HashMap<Integer, String> entityIndexToName;

    @Override
    public void setup() {
        double[][] positiveValues = setupPositiveValues();
        double[][] negaitveValues = setupNegativeValues();
        double[][] partials = setupPartials();
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
}
