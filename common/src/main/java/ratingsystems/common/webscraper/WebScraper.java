package ratingsystems.common.webscraper;

import java.io.IOException;
import java.util.HashMap;

public abstract class WebScraper {

    public void fetch(int year) throws IOException {
        throw new NoSuchMethodError();
    }

    public HashMap<String, String> fetch(String url) throws IOException {
        throw new NoSuchMethodError();
    }

    public void fetchBracket(int year) throws IOException {
        throw new NoSuchMethodError();
    }
}
