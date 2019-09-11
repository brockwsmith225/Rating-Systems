package ratingsystems.common.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeamsScraper extends WebScraper {

    @Override
    public HashMap<String, String> fetch(String url) throws IOException {
        HashMap<String, String> teams = new HashMap<>();
        Document d = Jsoup.connect(url).get();
        Elements elements = d.select("#schools tr");
        for (Element element : elements) {
            Elements elems = element.select("td");
            for (Element elem : elems) {
                String s = elem.toString();
                Pattern p1 = Pattern.compile("/\">(.*)</a>");
                Matcher m1 = p1.matcher(s);
                Pattern p2 = Pattern.compile("/schools/(.*)/\">");
                Matcher m2 = p2.matcher(s);
                if (m1.find() && m2.find()) {
                    teams.put(m1.group(1).replace("&amp;", "&"), m2.group(1));
                }
            }
        }
        return teams;
    }

}
