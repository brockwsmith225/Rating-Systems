package ratingsystems.common.cli;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParameterMap implements Map<String, Parameter> {

    private HashMap<String, Parameter> parameters;

    public ParameterMap() {
        parameters = new HashMap<>();
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
        return parameters.get(key);
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
            ParameterMap map = (ParameterMap)object;
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
        return parameters.hashCode();
    }

    public ParameterMap copy() {
        ParameterMap copy = new ParameterMap();
        for (String key : parameters.keySet()) {
            copy.put(key, parameters.get(key));
        }
        return copy;
    }

}
