package interpreter.datatypes;

import java.util.HashMap;

/**
 * A representation of a data point which compares two entities.
 */
public class DataPoint {
    private String otherEntity;
    private double score, otherScore, weightedScoreDiff;
    private int time;
    private HashMap<String, Double> otherData;

    /**
     *  Creates a new data point.
     *
     * @param otherEntity the name of the other entity of the data point
     * @param score the score of the entity the data point belongs to
     * @param otherScore the score of the other entity of the data point
     * @param weightedScoreDiff the weighted difference of scores
     * @param time the time at which the data point occurred
     */
    public DataPoint(String otherEntity, double score, double otherScore, double weightedScoreDiff, int time) {
        this.otherEntity = otherEntity;
        this.score = score;
        this.otherScore = otherScore;
        this.weightedScoreDiff = weightedScoreDiff;
        this.time = time;
    }

    /**
     * Returns the name of the other entity of the data point
     *
     * @return the name of the other entity of the data point
     */
    public String getOtherEntity() {
        return otherEntity;
    }

    /**
     * Returns the score of the entity the data point belongs to
     *
     * @return the score of the entity the data point belongs to
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns the score of the other entity of the data point
     *
     * @return the score of the other entity of the data point
     */
    public double getOtherScore() {
        return otherScore;
    }

    /**
     * Returns the weighted difference of scores
     *
     * @return the weighted difference of scores
     */
    public double getWeightedScoreDiff() {
        return weightedScoreDiff;
    }

    /**
     * Returns the time at which the data point occurred
     *
     * @return the time at which the data point occurred
     */
    public int getTime() {
        return time;
    }

    /**
     * Returns a copy of the data point
     *
     * @param dataPoint the data point to be copied
     *
     * @return a copy of the data point
     */
    static DataPoint copyOf(DataPoint dataPoint) {
        return new DataPoint(dataPoint.otherEntity, dataPoint.score, dataPoint.otherScore, dataPoint.weightedScoreDiff, dataPoint.time);
    }
}
