package ratingsystems.common.collegebasketball;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ratingsystems.common.webscraper.TeamsScraper;
import ratingsystems.common.webscraper.WebScraper;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollegeBasketballScraper extends WebScraper {

    @Override
    public void fetch(int year) throws IOException {
        WebScraper scraper = new TeamsScraper();
        HashMap<String, String> teams = scraper.fetch("https://www.sports-reference.com/cbb/schools/");
        HashMap<String, String> linkToName = new HashMap<>();
        for (String name : teams.keySet()) {
            linkToName.put(teams.get(name), name);
        }
        PrintStream file = new PrintStream(new File("src/data/cbb-" + year + ".csv"));
        for (String name : teams.keySet()) {
            String team = teams.get(name);
            ArrayList<ArrayList<String>> schedule = new ArrayList<>();
            Document d = Jsoup.connect("https://www.sports-reference.com/cbb/schools/" + team + "/" + year + "-gamelogs.html").get();
            Matcher c = Pattern.compile("conferences\\/.*\\/" + year + "\\.html\">(.*)<\\/a>").matcher((d.select("#meta").html()));
            String conference = c.find() ? c.group(1) : "";
            Elements offense = d.select("#sgl-basic td");
            HashMap<String, Pattern> data = new HashMap<>();
            data.put("date_game", Pattern.compile("\\.html\">(.*)<\\/a>"));
            data.put("game_location", Pattern.compile("game_location\">(.*)<\\/td>"));
            data.put("opp_id", Pattern.compile("schools\\/(.*)/" + year + ".html\">"));
            data.put("game_result", Pattern.compile("\">(.*)<\\/td>"));
            data.put("pts", Pattern.compile("pts\">(.*)<\\/td>"));
            data.put("opp_pts", Pattern.compile("opp_pts\">(.*)<\\/td>"));
            data.put("fg", Pattern.compile("fg\">(.*)<\\/td>"));
            data.put("fga", Pattern.compile("fga\">(.*)<\\/td>"));
            data.put("fg3", Pattern.compile("fg3\">(.*)<\\/td>"));
            data.put("fg3a", Pattern.compile("fg3a\">(.*)<\\/td>"));
            data.put("ft", Pattern.compile("ft\">(.*)<\\/td>"));
            data.put("fta", Pattern.compile("fta\">(.*)<\\/td>"));
            data.put("orb", Pattern.compile("orb\">(.*)<\\/td>"));
            data.put("trb", Pattern.compile("trb\">(.*)<\\/td>"));
            data.put("ast", Pattern.compile("ast\">(.*)<\\/td>"));
            data.put("stl", Pattern.compile("stl\">(.*)<\\/td>"));
            data.put("blk", Pattern.compile("blk\">(.*)<\\/td>"));
            data.put("tov", Pattern.compile("tov\">(.*)<\\/td>"));
            data.put("pf", Pattern.compile("pf\">(.*)<\\/td>"));
            data.put("opp_fg", Pattern.compile("opp_fg\">(.*)<\\/td>"));
            data.put("opp_fga", Pattern.compile("opp_fga\">(.*)<\\/td>"));
            data.put("opp_fg3", Pattern.compile("opp_fg3\">(.*)<\\/td>"));
            data.put("opp_fg3a", Pattern.compile("opp_fg3a\">(.*)<\\/td>"));
            data.put("opp_ft", Pattern.compile("opp_ft\">(.*)<\\/td>"));
            data.put("opp_fta", Pattern.compile("opp_fta\">(.*)<\\/td>"));
            data.put("opp_orb", Pattern.compile("opp_orb\">(.*)<\\/td>"));
            data.put("opp_trb", Pattern.compile("opp_trb\">(.*)<\\/td>"));
            data.put("opp_ast", Pattern.compile("opp_ast\">(.*)<\\/td>"));
            data.put("opp_stl", Pattern.compile("opp_stl\">(.*)<\\/td>"));
            data.put("opp_blk", Pattern.compile("opp_blk\">(.*)<\\/td>"));
            data.put("opp_tov", Pattern.compile("opp_tov\">(.*)<\\/td>"));
            data.put("opp_pf", Pattern.compile("opp_pf\">(.*)<\\/td>"));
            schedule.add(new ArrayList<>());
            for (Element elem : offense) {
                for (String attr : data.keySet()) {
                    if (elem.attr("data-stat").equals(attr)) {
                        String s = elem.toString();
                        Matcher m = data.get(attr).matcher(s);
                        if (m.find()) {
                            if (attr.equals("game_result")) {
                                schedule.get(schedule.size() - 1).add(m.group(1).substring(0, 1));
                            } else {
                                schedule.get(schedule.size() - 1).add(m.group(1).replace("&amp;", "&"));
                            }
                            if (attr.equals("opp_pf")) {
                                schedule.add(new ArrayList<>());
                            }
                        }
                    }
                }
            }
            if (!conference.equals("")) {
                String res = "";
                for (int g = 0; g < schedule.size(); g++) {
                    ArrayList<String> game = schedule.get(g);
                    if (game.size() == data.size()) {
                        game.add(1, name);
                        game.set(2, game.get(2).equals("") ? "H" : game.get(2).equals("N") ? "N" : "A");
                        game.set(3, linkToName.get(game.get(3)));
                        game.add(2, conference);
                        for (int i = 0; i < game.size(); i++) {
                            res += game.get(i) + ",";
                        }
                        res = res.substring(0, res.length() - 1) + "\n";
                    }
                }
                System.out.println(res.substring(0, res.length() - 1));
            }
        }
    }

}