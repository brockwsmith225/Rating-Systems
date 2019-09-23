package ratingsystems.common.cli.parameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParameterMap implements Map<String, Parameter> {

    private HashMap<String, Parameter> parameters;

    public ParameterMap() {
        parameters = new HashMap<>();
    }

    public void setParameterValue(String parameter, Object value) {
        if (parameters.containsKey(parameter)) {
            parameters.get(parameter).setValue(value);
        }
    }

    public void setParameterValue(String parameter, String value) {
        if (parameters.containsKey(parameter)) {
            parameters.get(parameter).setValue(value);
        }
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
        return parameters.put(key, value);
    }

    @Override
    public Parameter remove(Object key) {
        return parameters.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Parameter> map) {
        parameters.putAll(map);
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
        if (object.getClass().equals(ParameterMap.class)) {
            ParameterMap map = (ParameterMap) object;
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

    public static ParameterMap copyOf(ParameterMap parameters) {
        ParameterMap copy = new ParameterMap();
        for (String key : parameters.keySet()) {
            copy.put(key, parameters.get(key));
        }
        return copy;
    }

}
