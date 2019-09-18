package ratingsystems.api;

public class Response {
    private final Object body;

    public Response() {
        this.body = "";
    }

    public Response(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return body;
    }
}
