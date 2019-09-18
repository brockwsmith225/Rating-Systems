package ratingsystems.api;

public class Error extends Response {
    private final String error;

    public Error(String error) {
        this.error = error;
    }

    public String getBody() {
        return error;
    }
}
