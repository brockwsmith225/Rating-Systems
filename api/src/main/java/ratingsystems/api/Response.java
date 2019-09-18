package ratingsystems.api;

public class Response {
    private final String body;

    public Response(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
