package ratingsystems.common.parameters;

import ratingsystems.common.interpreter.Location;

import java.util.*;

public class Parameters implements Map<String, Parameter> {

    private static HashMap<String, Parameter> defaultParameters;
    public static Set<String> leagues;

    private HashMap<String, Parameter> parameters;

    public Parameters() {
        defaultParameters = new HashMap<>();
        defaultParameters.put("YEAR", new IntegerParameter(Calendar.getInstance().get(Calendar.YEAR), 1800, 2500));
        defaultParameters.put("WEEK", new IntegerParameter(0, 0, 50));
        defaultParameters.put("LEAGUE", new StringParameter("cfb", leagues));
        defaultParameters.put("START_YEAR", new IntegerParameter(2014, 1800, 2500));
        defaultParameters.put("LOCATION", new LocationParameter(Location.NEUTRAL));

        parameters = new HashMap<>();
        parameters.put("YEAR", defaultParameters.get("YEAR"));
        parameters.put("LEAGUE", defaultParameters.get("LEAGUE"));
    }

    public boolean setParameterValue(String parameter, Object value) {
        if (parameters.containsKey(parameter)) {
            return parameters.get(parameter).setValue(value);
        } else if (defaultParameters.containsKey(parameter)) {
            Parameter p = defaultParameters.get(parameter).copy();
            if (p.validateValue(value)) {
                p.setValue(value);
                parameters.put(parameter, p);
            }
        }
        return false;
    }

    public boolean setParameterValue(String parameter, String value) {
        if (parameters.containsKey(parameter)) {
            return parameters.get(parameter).setValue(value);
        } else if (defaultParameters.containsKey(parameter)) {
            Parameter p = defaultParameters.get(parameter).copy();
            if (p.validateValue(value)) {
                p.setValue(value);
                parameters.put(parameter, p);
            }
        }
        return false;
    }

    public static boolean setDefaultParameterValue(String parameter, Object value) {
        if (defaultParameters.containsKey(parameter)) {
            return defaultParameters.get(parameter).setValue(value);
        }
        return false;
    }

    public static boolean setDefaultParameterValue(String parameter, String value) {
        if (defaultParameters.containsKey(parameter)) {
            return defaultParameters.get(parameter).setValue(value);
        }
        return false;
    }

    public static boolean isValidParameter(String parameter) {
        return defaultParameters.containsKey(parameter);
    }

    public static boolean isValidParameterValue(String parameter, Object value) {
        if (defaultParameters.containsKey(parameter)) {
            return defaultParameters.get(parameter).validateValue(value);
        }
        return false;
    }

    public static boolean isValidParameterValue(String parameter, String value) {
        if (defaultParameters.containsKey(parameter)) {
            return defaultParameters.get(parameter).validateValue(value);
        }
        return false;
    }

    @Override
    public int size() {
        return parameters.size();
    }

    @Override
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return parameters.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return parameters.containsValue(value);
    }

    @Override
    public Parameter get(Object key) {
        return parameters.get(key).copy();
    }

    public Object getValue(Object key) {
        return parameters.get(key).getValue();
    }

    @Override
    public Parameter put(String key, Parameter value) {
        if (defaultParameters.containsKey(key)) {
            return parameters.put(key, value);
        }
        return null;
    }

    @Override
    public Parameter remove(Object key) {
        return parameters.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Parameter> map) {
        for (String key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    @Override
    public void clear() {
        parameters.clear();
    }

    @Override
    public Set<String> keySet() {
        return parameters.keySet();
    }

    @Override
    public Collection<Parameter> values() {
        return parameters.values();
    }

    @Override
    public Set<Entry<String, Parameter>> entrySet() {
        return parameters.entrySet();
    }

    @Override
    public boolean equals(Object object) {
        if (object.getClass().equals(Parameters.class)) {
            Parameters map = (Parameters) object;
            for (String key : parameters.keySet()) {
                if (!map.containsKey(key)) {
                    return false;
                }
                if (!parameters.get(key).equals(map.get(key))) {
                    return false;
                }
            }
            return true;
        }
        return parameters.equals(object);
    }

    @Override
    public int hashCode() {
        String hashCode = "";
        for (String parameter : parameters.keySet()) {
            hashCode += parameter + parameters.get(parameter).getValue();
        }
        return hashCode.hashCode();
    }

    public static Parameters copyOf(Parameters parameters) {
        Parameters copy = new Parameters();
        for (String key : parameters.keySet()) {
            copy.put(key, parameters.get(key));
        }
        return copy;
    }

}
