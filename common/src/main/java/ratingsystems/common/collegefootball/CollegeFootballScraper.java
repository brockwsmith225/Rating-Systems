package ratingsystems.common.collegefootball;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ratingsystems.common.webscraper.TeamsScraper;
import ratingsystems.common.webscraper.WebScraper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollegeFootballScraper extends WebScraper {

    @Override
    public void fetch(int year) throws IOException {
        WebScraper scraper = new TeamsScraper();
        HashMap<String, String> teams = scraper.fetch("https://www.sports-reference.com/cfb/schools/");
        HashMap<String, String> linkToName = new HashMap<>();
        for (String name : teams.keySet()) {
            linkToName.put(teams.get(name), name);
        }
        PrintStream file = new PrintStream(new File("src/data/cfb-" + year + ".csv"));
        file.println("Date,Team,Conference,Location,Opponent,Result,Points,OpponentPoints,PassCompletions,PassAttempts,PassYards,PassTDs,RushAttempts,RushYards,RushTDs,FirstDownPass,FirstDownRush,Penalties,PenaltyYards,Fumbles,Interceptions");
        for (String name : teams.keySet()) {
            String team = teams.get(name);
            ArrayList<ArrayList<String>> schedule = new ArrayList<>();
            Document d = Jsoup.connect("https://www.sports-reference.com/cfb/schools/" + team + "/" + year + "/gamelog/").get();
            Matcher c = Pattern.compile("conferences\\/.*\\/" + year + "\\.html\">(.*)<\\/a>").matcher((d.select("#meta").html()));
            String conference = c.find() ? c.group(1) : "";
            Elements offense = d.select("#offense td");
            HashMap<String, Pattern> data = new HashMap<>();
            data.put("date_game", Pattern.compile("\\.html\">(.*)<\\/a>"));
            data.put("game_location", Pattern.compile("game_location\">(.*)<\\/td>"));
            data.put("opp_name", Pattern.compile("schools\\/(.*)/" + year + ".html\">"));
            data.put("game_result", Pattern.compile("\">(.*)<\\/td>"));
            data.put("pass_cmp", Pattern.compile("pass_cmp\">(.*)<\\/td>"));
            data.put("pass_att", Pattern.compile("pass_att\">(.*)<\\/td>"));
            data.put("pass_yds", Pattern.compile("pass_yds\">(.*)<\\/td>"));
            data.put("pass_td", Pattern.compile("pass_td\">(.*)<\\/td>"));
            data.put("rush_att", Pattern.compile("rush_att\">(.*)<\\/td>"));
            data.put("rush_yds", Pattern.compile("rush_yds\">(.*)<\\/td>"));
            data.put("rush_td", Pattern.compile("rush_td\">(.*)<\\/td>"));
            data.put("first_down_pass", Pattern.compile("first_down_pass\">(.*)<\\/td>"));
            data.put("first_down_rush", Pattern.compile("first_down_rush\">(.*)<\\/td>"));
            data.put("penalty", Pattern.compile("penalty\">(.*)<\\/td>"));
            data.put("penalty_yds", Pattern.compile("penalty_yds\">(.*)<\\/td>"));
            data.put("fumbles_lost", Pattern.compile("fumbles_lost\">(.*)<\\/td>"));
            data.put("pass_int", Pattern.compile("pass_int\">(.*)<\\/td>"));
            schedule.add(new ArrayList<>());
            for (Element elem : offense) {
                for (String attr : data.keySet()) {
                    if (elem.attr("data-stat").equals(attr)) {
                        String s = elem.toString();
                        Matcher m = data.get(attr).matcher(s);
                        if (m.find()) {
                            schedule.get(schedule.size() - 1).add(m.group(1).replace("&amp;", "&"));
                            if (attr.equals("pass_int")) {
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
                        String[] result = game.get(4).split(" ");
                        game.set(4, result[0]);
                        String[] scores = result[1].replace("(", "").replace(")", "").split("-");
                        game.add(5, scores[0]);
                        game.add(6, scores[1]);
                        game.add(2, conference);
                        for (int i = 0; i < game.size(); i++) {
                            res += game.get(i) + ",";
                        }
                        res = res.substring(0, res.length() - 1) + "\n";
                    }
                }
                file.println(res.substring(0, res.length() - 1));
            }
        }
    }

}
