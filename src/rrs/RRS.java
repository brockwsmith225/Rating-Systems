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
        double[][] partials = setupPartials(entities.size());
    }

    @Override
    public void rankEntities() {}

    @Override
    public void rankGroups() {}



    //========== RRS only methods ==========
    private double[][] setupPositiveValues(int numberOfEntities) {
        double[][] values = new double[numberOfEntities][numberOfEntities];
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

    private double[][] setupPartials(int numberOfEntities) {
        double[][] partials = new double[numberOfEntities][numberOfEntities];
        for (int r = 0; r < numberOfEntities; r++) {
            for (int c = 0; c < numberOfEntities; c++) {
                partials[r][c] = 1.0 / numberOfEntities;
            }
        }
        return partials;
    }
}
