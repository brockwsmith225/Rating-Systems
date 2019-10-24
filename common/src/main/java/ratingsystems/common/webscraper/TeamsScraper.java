package ratingsystems.common.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeamsScraper extends WebScraper {

    public HashMap<String, String[]> fetch(String url, int year) throws IOException {
        HashMap<String, String[]> teams = new HashMap<>();
        Document d = Jsoup.connect(url).get();
        Elements elements = d.select("#schools tr");
        for (Element element : elements) {
            String s = element.toString();
            Pattern p = Pattern.compile("/\">(.*)</a>");
            Matcher name = p.matcher(s);
            p = Pattern.compile("/schools/(.*)/\">");
            Matcher link = p.matcher(s);
            p = Pattern.compile("year_min\">(.*)<");
            Matcher startYear = p.matcher(s);
            p = Pattern.compile("year_max\">(.*)<");
            Matcher endYear = p.matcher(s);
            if (name.find() && link.find() && startYear.find() && endYear.find()) {
                if (Integer.parseInt(startYear.group(1)) <= year && Integer.parseInt(endYear.group(1)) >= year) {
                    teams.put(name.group(1).replace("&amp;", "&"), new String[]{link.group(1), "", ""});
                }
            }
        }

        HashSet<String> removedTeams = new HashSet<>();
        for (String name : teams.keySet()) {
            try {
                d = Jsoup.connect(url + teams.get(name)[0] + "/" + year + ".html").get();

                Matcher c = Pattern.compile("conferences\\/.*\\/" + year + "\\.html\">(.*)<\\/a>").matcher((d.select("#meta").html()));
                teams.get(name)[1] = c.find() ? c.group(1) : "";

                c = Pattern.compile("coaches\\/.*\\.html\">(.*)<\\/a>").matcher((d.select("#meta").html()));
                teams.get(name)[2] = c.find() ? c.group(1) : "";
            } catch (org.jsoup.HttpStatusException e) {
                removedTeams.add(name);
            }
        }
        for (String team : removedTeams) {
            teams.remove(team);
        }
        return teams;
    }

}
