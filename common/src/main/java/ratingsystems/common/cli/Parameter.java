package ratingsystems.common.cli;

import java.util.HashSet;
import java.util.Set;

public class Parameter {
    private Object value;
    private Comparable comparableValue, minimum, maximum;
    private Set<Object> possibleValues;
    private int validationMode;
    private Class type;

    public Parameter(Class type, Object value) {
        this.type = type;
        this.value = value;
        this.validationMode = 0;
    }

    public Parameter(Class type, Comparable value, Comparable minimum, Comparable maximum) {
        this.type = type;
        this.comparableValue = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.validationMode = 1;
    }

    public Parameter(Class type, Object value, Set<?> possibleValues) {
        this.type = type;
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
        if (validationMode == 0) {
            this.value = value;
        } else if (validationMode == 1) {
            if (value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0) {
                this.comparableValue = value;
            }
        } else if (validationMode == 2) {
            if (possibleValues.contains(value)) {
                this.value = value;
            }
        }
    }

    public boolean validateValue(Object value) {
        if (validationMode == 0) {
            return true;
        } else if (validationMode == 2) {
            if (possibleValues.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean validateValue(Comparable value) {
        if (validationMode == 0) {
            return true;
        } else if (validationMode == 1) {
            if (value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0) {
                return true;
            }
        } else if (validationMode == 2) {
            if (possibleValues.contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (validationMode == 1) {
            return comparableValue.toString();
        }
        return value.toString();
    }

    public boolean equals(Parameter parameter) {
        return this.getValue().equals(parameter.getValue());
    }

    public Class getType() {
        return this.type;
    }
}
