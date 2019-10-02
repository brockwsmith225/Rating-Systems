package ratingsystems.common.parameters;

import ratingsystems.common.interpreter.Location;

public class LocationParameter implements Parameter<Location> {

    Location value;

    public LocationParameter(Location value) {
        this.value = value;
    }

    public Location getValue() {
        return value;
    }

    public boolean setValue(Location value) {
        if (validateValue(value)) {
            this.value = value;
            return true;
        }
        return false;
    }

    public boolean setValue(String value) {
        if (validateValue(value)) {
            this.value = parseValue(value);
            return true;
        }
        return false;
    }

    public Location parseValue(String value) {
        switch(value.toUpperCase().charAt(0)) {
            case 'H':
                return Location.HOME;
            case 'A':
                return Location.AWAY;
            case 'N':
                return Location.NEUTRAL;
        }
        return null;
    }

    public boolean validateValue(Location value) {
        return true;
    }

    public boolean validateValue(String value) {
        return parseValue(value) != null;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    public boolean equals(Parameter parameter) {
        return this.value == parameter.getValue();
    }

    public Parameter<Location> copy() {
        return new LocationParameter(this.value);
    }

}
