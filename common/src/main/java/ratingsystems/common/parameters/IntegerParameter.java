package ratingsystems.common.parameters;

public class IntegerParameter implements Parameter<Integer> {
    private int value, minimum, maximum;

    public IntegerParameter(int value, int minimum, int maximum) {
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Integer getValue() {
        return value;
    }

    public boolean setValue(Integer value) {
        if (validateValue(value)) {
            this.value = value;
            return true;
        }
        return false;
    }

    public boolean setValue(String value) {
        if (parseValue(value) != null) {
            return setValue(parseValue(value));
        }
        return false;
    }

    public boolean validateValue(Integer value) {
        return value >= minimum && value <= maximum;
    }

    public boolean validateValue(String value) {
        if (parseValue(value) != null) {
            return validateValue(parseValue(value));
        }
        return false;
    }

    public Integer parseValue(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid value " + value + " for type Integer");
            return null;
        }
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public boolean equals(Parameter parameter) {
        return this.getValue().equals(parameter.getValue());
    }

    public IntegerParameter copy() {
        return new IntegerParameter(value, minimum, maximum);
    }

}

