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
        HashMap<String, String[]> teams = new TeamsScraper().fetch("https://www.sports-reference.com/cfb/schools/", year);
        HashMap<String, String> linkToName = new HashMap<>();
        for (String name : teams.keySet()) {
            linkToName.put(teams.get(name)[0], name);
        }
        PrintStream file = new PrintStream(new File("data/cfb-" + year + ".csv"));
        if (year >= 2000) {
            file.println("Date,Team,Conference,Coach,Location,Opponent,OpponentConference,OpponentCoach,Result,Points,OpponentPoints,PassCompletions,PassAttempts,PassYards,PassTDs,RushAttempts,RushYards,RushTDs,FirstDownPass,FirstDownRush,Penalties,PenaltyYards,Fumbles,Interceptions");
            for (String name : teams.keySet()) {
                String team = teams.get(name)[0];
                String conference = teams.get(name)[1];
                String coach = teams.get(name)[2];
                ArrayList<ArrayList<String>> schedule = new ArrayList<>();
                Document d = Jsoup.connect("https://www.sports-reference.com/cfb/schools/" + team + "/" + year + "/gamelog/").get();
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
                    StringBuilder res = new StringBuilder();
                    for (ArrayList<String> game : schedule) {
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
                            game.add(3, coach);
                            game.add(6, teams.get(game.get(5))[1]);
                            game.add(7, teams.get(game.get(5))[2]);
                            for (String entry : game) {
                                res.append(entry);
                                res.append(",");
                            }
                            res = res.replace(res.length() - 1, res.length(), "\n");
                        }
                    }
                    res.deleteCharAt(res.length() - 1);
                    file.println(res.toString());
                }
            }
        } else {
            file.println("Date,Team,Conference,Coach,Location,Opponent,OpponentConference,OpponentCoach,Result,Points,OpponentPoints");
            for (String name : teams.keySet()) {
                String team = teams.get(name)[0];
                String conference = teams.get(name)[1];
                String coach = teams.get(name)[2];
                ArrayList<ArrayList<String>> schedule = new ArrayList<>();
                Document d = Jsoup.connect("https://www.sports-reference.com/cfb/schools/" + team + "/" + year + "-schedule.html").get();
                Elements offense = d.select("#schedule td");
                HashMap<String, Pattern> data = new HashMap<>();
                data.put("date_game", Pattern.compile("/cfb/boxscores/([0-9]{4}-[0-9]{2}-[0-9]{2})-.+\\.html"));
                data.put("game_location", Pattern.compile("game_location\">(.*)<\\/td>"));
                data.put("opp_name", Pattern.compile("schools\\/(.*)/" + year + ".html\">"));
                data.put("game_result", Pattern.compile("game_result\">(.*)<\\/td>"));
                data.put("points", Pattern.compile("points\">(.*)<\\/td>"));
                data.put("opp_points", Pattern.compile("opp_points\">(.*)<\\/td>"));
                schedule.add(new ArrayList<>());
                for (Element elem : offense) {
                    for (String attr : data.keySet()) {
                        if (elem.attr("data-stat").equals(attr)) {
                            String s = elem.toString();
                            Matcher m = data.get(attr).matcher(s);
                            if (m.find()) {
                                schedule.get(schedule.size() - 1).add(m.group(1).replace("&amp;", "&"));
                                if (attr.equals("opp_points")) {
                                    schedule.add(new ArrayList<>());
                                }
                            }
                        }
                    }
                }
                if (!conference.equals("")) {
                    StringBuilder res = new StringBuilder();
                    for (ArrayList<String> game : schedule) {
                        if (game.size() == data.size()) {
                            game.add(1, name);
                            game.set(2, game.get(2).equals("") ? "H" : game.get(2).equals("N") ? "N" : "A");
                            game.set(3, linkToName.get(game.get(3)));
                            game.add(2, conference);
                            game.add(3, coach);
                            game.add(6, teams.get(game.get(5))[1]);
                            game.add(7, teams.get(game.get(5))[2]);
                            for (String entry : game) {
                                res.append(entry);
                                res.append(",");
                            }
                            res = res.replace(res.length() - 1, res.length(), "\n");
                        }
                    }
                    file.print(res.toString());
                }
            }
        }
    }

}
