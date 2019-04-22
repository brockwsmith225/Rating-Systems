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
        double[][] positiveValues = new double[numberOfEntities][numberOfEntities];
        for (String entity : entities.keySet()) {
            ArrayList<DataPoint> dataPoints = entities.get(entity).getDataPoints();
            for (DataPoint dataPoint : dataPoints) {
                if (dataPoint.getWeightedScoreDiff() > 0) {
                    positiveValues[entityNameToIndex.get(entity)][entityNameToIndex.get(dataPoint.getOtherEntity())] = Math.abs(dataPoint.getWeightedScoreDiff());
                }
            }
        }
        return positiveValues;
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
