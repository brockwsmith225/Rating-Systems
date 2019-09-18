package ratingsystems.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @RequestMapping("/api")
    public Response request() {
        return new Response("test");
    }

}
