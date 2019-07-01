package ratingsystems.common.cli;

import java.util.HashSet;
import java.util.Set;

public class Parameter {
    private Object value;
    private Comparable comparableValue, minimum, maximum;
    private Set<Object> possibleValues;
    private int validationMode;

    public Parameter(Object value) {
        this.value = value;
        this.validationMode = 0;
    }

    public Parameter(Comparable value, Comparable minimum, Comparable maximum) {
        this.comparableValue = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.validationMode = 1;
    }

    public Parameter(Object value, Set<? extends Object> possibleValues) {
        this.value = value;
        this.possibleValues = new HashSet<>();
        for (Object possibleValue : possibleValues) {
            this.possibleValues.add(possibleValue);
        }
        this.validationMode = 2;
    }

    public Object getValue() {
        if (validationMode == 1) {
            return comparableValue;
        }
        return value;
    }

    public void setValue(Object value) {
        if (validationMode == 0) {
            this.value = value;
        } else if (validationMode == 2) {
            if (possibleValues.contains(value)) {
                this.value = value;
            }
        }
    }

    public void setValue(Comparable value) {
        if (value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0) {
            this.comparableValue = value;
        }
    }
}