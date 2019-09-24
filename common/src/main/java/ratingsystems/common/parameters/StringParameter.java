package ratingsystems.common.parameters;

import java.util.HashSet;
import java.util.Set;

public class StringParameter implements Parameter<String> {

    private String value;
    private Set<String> possibleValues;

    public StringParameter(String value, Set<String> possibleValues) {
        this.value = value;
        this.possibleValues = new HashSet<>();
        this.possibleValues.addAll(possibleValues);
    }

    public String getValue() {
        return this.value;
    }

    public boolean setValue(String value) {
        if (validateValue(value)) {
            this.value = value;
            return true;
        }
        return false;
    }

    public boolean validateValue(String value) {
        return this.possibleValues.contains(value);
    }

    public String parseValue(String value) {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public boolean equals(Parameter parameter) {
        return this.getValue().equals(parameter.getValue());
    }

    public StringParameter copy() {
        return new StringParameter(value, possibleValues);
    }

}
