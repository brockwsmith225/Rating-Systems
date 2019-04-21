package interpreter;

import interpreter.datatypes.DataPoint;

public interface LimitingFunction {
    /**
     * Determines whether or not the data point should be included
     *
     * @param dataPoint the data point to be considered
     * @return true if the data point should be included, false otherwise
     */
    boolean includeData(DataPoint dataPoint);
}
