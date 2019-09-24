package ratingsystems.common.cli.parameters;

public interface Parameter<T> {

    public T getValue();

    public boolean setValue(T value);

    public boolean setValue(String value);

    public T parseValue(String value);

    public boolean validateValue(T value);

    public boolean validateValue(String value);

    @Override
    public String toString();

    public boolean equals(Parameter parameter);

    public Parameter<T> copy();
}
