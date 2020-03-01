package ratingsystems.common.interpreter;

import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bracket {
    private Bracket subbracket1, subbracket2;
    private String team1, team2;
    private int seed1, seed2;
    private Map<String, Double> odds;
    private Location location;
    private String bracketName;

    private Bracket() {
        this.team1 = "";
        this.team2 = "";
        this.seed1 = -1;
        this.seed2 = -1;
        this.subbracket1 = this;
        this.subbracket2 = this;
        this.odds = new HashMap<>();
        this.location = Location.NEUTRAL;
        this.bracketName = "";
    }

    public Bracket(String team1, String team2) {
        this();
        if (team1 == null)
            team1 = "BYE";
        if (team2 == null)
            team2 = "BYE";
        this.team1 = team1;
        this.team2 = team2;
    }

    public Bracket(String team1, Bracket subbracket2) {
        this();
        if (team1 == null)
            team1 = "BYE";
        this.team1 = team1;
        this.subbracket2 = subbracket2;
    }

    public Bracket(Bracket subbracket1, String team2) {
        this();
        if (team2 == null)
            team2 = "BYE";
        this.team2 = team2;
        this.subbracket1 = subbracket1;
    }

    public Bracket(Bracket subbracket1, Bracket subbracket2) {
        this();
        this.subbracket1 = subbracket1;
        this.subbracket2 = subbracket2;
        if (this.subbracket1.bracketName.equals(this.subbracket2.bracketName))
            this.bracketName = this.subbracket1.bracketName;
    }

    public Bracket(String team1, int seed1, String team2, int seed2) {
        this(team1, team2);
        this.seed1 = seed1;
        this.seed2 = seed2;
    }

    public Bracket(String team1, int seed1, Bracket subbracket2) {
        this(team1, subbracket2);
        this.seed1 = seed1;
    }

    public Bracket(Bracket subbracket1, String team2, int seed2) {
        this(subbracket1, team2);
        this.seed2 = seed2;
    }

    public Bracket(String team1, String team2, Location location) {
        this(team1, team2);
        this.location = location;
    }

    public Bracket(String team1, Bracket subbracket2, Location location) {
        this(team1, subbracket2);
        this.location = location;
    }

    public Bracket(Bracket subbracket1, String team2, Location location) {
        this(subbracket1, team2);
        this.location = location;
    }

    public Bracket(Bracket subbracket1, Bracket subbracket2, Location location) {
        this(subbracket1, subbracket2);
        this.location = location;
    }

    public Bracket(String team1, int seed1, String team2, int seed2, Location location) {
        this(team1, seed1, team2, seed2);
        this.location = location;
    }

    public Bracket(String team1, int seed1, Bracket subbracket2, Location location) {
        this(team1, seed1, subbracket2);
        this.location = location;
    }

    public Bracket(Bracket subbracket1, String team2, int seed2, Location location) {
        this(subbracket1, team2, seed2);
        this.location = location;
    }

    public Bracket(String team1, String team2, String bracketName) {
        this(team1, team2);
        this.bracketName = bracketName;
    }

    public Bracket(String team1, Bracket subbracket2, String bracketName) {
        this(team1, subbracket2);
        this.bracketName = bracketName;
    }

    public Bracket(Bracket subbracket1, String team2, String bracketName) {
        this(subbracket1, team2);
        this.bracketName = bracketName;
    }

    public Bracket(Bracket subbracket1, Bracket subbracket2, String bracketName) {
        this(subbracket1, subbracket2);
        this.bracketName = bracketName;
    }

    public Bracket(String team1, int seed1, String team2, int seed2, String bracketName) {
        this(team1, seed1, team2, seed2);
        this.bracketName = bracketName;
    }

    public Bracket(String team1, int seed1, Bracket subbracket2, String bracketName) {
        this(team1, seed1, subbracket2);
        this.bracketName = bracketName;
    }

    public Bracket(Bracket subbracket1, String team2, int seed2, String bracketName) {
        this(subbracket1, team2, seed2);
        this.bracketName = bracketName;
    }

    public Bracket(String team1, String team2, Location location, String bracketName) {
        this(team1, team2, location);
        this.bracketName = bracketName;
    }

    public Bracket(String team1, Bracket subbracket2, Location location, String bracketName) {
        this(team1, subbracket2, location);
        this.bracketName = bracketName;
    }

    public Bracket(Bracket subbracket1, String team2, Location location, String bracketName) {
        this(subbracket1, team2, location);
        this.bracketName = bracketName;
    }

    public Bracket(Bracket subbracket1, Bracket subbracket2, Location location, String bracketName) {
        this(subbracket1, subbracket2, location);
        this.bracketName = bracketName;
    }

    public Bracket(String team1, int seed1, String team2, int seed2, Location location, String bracketName) {
        this(team1, seed1, team2, seed2, location);
        this.bracketName = bracketName;
    }

    public Bracket(String team1, int seed1, Bracket subbracket2, Location location, String bracketName) {
        this(team1, seed1, subbracket2, location);
        this.bracketName = bracketName;
    }

    public Bracket(Bracket subbracket1, String team2, int seed2, Location location, String bracketName) {
        this(subbracket1, team2, seed2, location);
        this.bracketName = bracketName;
    }

    public List<String> getTeams() {
        List<String> teams = new ArrayList<>();
        if (subbracket1 != this) {
            teams.addAll(subbracket1.getTeams());
        } else if (!team1.equals("BYE")) {
            teams.add(team1);
        }
        if (subbracket2 != this) {
            teams.addAll(subbracket2.getTeams());
        } else if (!team2.equals("BYE")) {
            teams.add(team2);
        }
        return teams;
    }

    public int size() {
        int size = 0;
        if (subbracket1 != this) {
            size += subbracket1.size();
        } else if (!team1.equals("BYE")) {
            size++;
        }
        if (subbracket2 != this) {
            size += subbracket2.size();
        } else if (!team2.equals("BYE")) {
            size++;
        }
        return size;
    }

    public double getOdds(String team) {
        if (odds.containsKey(team)) {
            return odds.get(team);
        }
        return -1.0;
    }

    public void evaluate(RatingSystem ratingSystem) {
        List<String> teams1 = new ArrayList<>();
        Map<String, Double> prevOdds = new HashMap<>();
        if (subbracket1 != this) {
            teams1 = subbracket1.getTeams();
            subbracket1.evaluate(ratingSystem);
            for (String team : teams1) {
                prevOdds.put(team, subbracket1.getOdds(team));
            }
        } else if (!team1.equals("BYE")) {
            teams1.add(team1);
            prevOdds.put(team1, 1.0);
        }
        List<String> teams2 = new ArrayList<>();
        if (subbracket2 != this) {
            teams2 = subbracket2.getTeams();
            subbracket2.evaluate(ratingSystem);
            for (String team : teams2) {
                prevOdds.put(team, subbracket2.getOdds(team));
            }
        } else if (!team2.equals("BYE")) {
            teams2.add(team2);
            prevOdds.put(team2, 1.0);
        }
        for (String team : teams1) {
            if (!ratingSystem.hasTeam(team)) {
                System.err.println("ERROR: Team " + team + " not found.");
            }
        }
        for (String team : teams2) {
            if (!ratingSystem.hasTeam(team)) {
                System.err.println("ERROR: Team " + team + " not found.");
            }
        }
        if (teams1.size() == 0) {
            for (String team : teams2)
                odds.put(team, subbracket2.getOdds(team));
        } else if (teams2.size() == 0) {
            for (String team : teams1)
                odds.put(team, subbracket1.getOdds(team));
        } else {
            for (String team : teams1) odds.put(team, 0.0);
            for (String team : teams2) odds.put(team, 0.0);
            for (String team1 : teams1) {
                for (String team2 : teams2) {
                    Prediction prediction = ratingSystem.predictGame(team1, team2, location);
                    double probOfGame = prevOdds.get(team1) * prevOdds.get(team2);
                    odds.put(team1, odds.get(team1) + probOfGame * prediction.getOdds());
                    odds.put(team2, odds.get(team2) + probOfGame * (1 - prediction.getOdds()));
                }
            }
        }
    }

    public Map<String, List<Double>> getFullOdds() {
        Map<String, List<Double>> odds = new HashMap<>();
        Map<String, List<Double>> subOdds1, subOdds2;
        int previousRounds = 0;
        if (subbracket1 == this) {
            subOdds1 = new HashMap<>();
            subOdds1.put(team1, new ArrayList<>());
        } else {
            subOdds1 = subbracket1.getFullOdds();
            for (String team : subOdds1.keySet()) {
                if (subOdds1.get(team).size() > previousRounds)
                    previousRounds = subOdds1.get(team).size();
            }
        }
        if (subbracket2 == this) {
            subOdds2 = new HashMap<>();
            subOdds2.put(team2, new ArrayList<>());
        } else {
            subOdds2 = subbracket2.getFullOdds();
            for (String team : subOdds2.keySet()) {
                if (subOdds2.get(team).size() > previousRounds)
                    previousRounds = subOdds2.get(team).size();
            }
        }
        for (String team : subOdds1.keySet()) {
            while (subOdds1.get(team).size() < previousRounds)
                subOdds1.get(team).add(0, 1.0);
            odds.put(team, subOdds1.get(team));
            odds.get(team).add(this.odds.get(team));
        }
        for (String team : subOdds2.keySet()) {
            while (subOdds2.get(team).size() < previousRounds)
                subOdds2.get(team).add(0, 1.0);
            odds.put(team, subOdds2.get(team));
            odds.get(team).add(this.odds.get(team));
        }
        return odds;
    }

    public Map<String, List<String>> getBracketNames() {
        Map<String, List<String>> bracketNames = new HashMap<>();
        if (subbracket1 != this) {
            Map<String, List<String>> subbracketNames = subbracket1.getBracketNames();
            for (String team : subbracketNames.keySet()) {
                bracketNames.put(team, subbracketNames.get(team));
                if (bracketName.length() > 0 && !bracketNames.get(team).contains(bracketName))
                    bracketNames.get(team).add(bracketName);
            }
        } else if (!team1.equals("BYE")) {
            bracketNames.put(team1, new ArrayList<>());
            if (bracketName.length() > 0)
                bracketNames.get(team1).add(bracketName);
        }
        if (subbracket2 != this) {
            Map<String, List<String>> subbracketNames = subbracket2.getBracketNames();
            for (String team : subbracketNames.keySet()) {
                bracketNames.put(team, subbracketNames.get(team));
                if (bracketName.length() > 0 && !bracketNames.get(team).contains(bracketName))
                    bracketNames.get(team).add(bracketName);
            }
        } else if (!team2.equals("BYE")) {
            bracketNames.put(team2, new ArrayList<>());
            if (bracketName.length() > 0)
                bracketNames.get(team2).add(bracketName);
        }
        return bracketNames;
    }

    public Map<String, Integer> getSeeds() {
        Map<String, Integer> seeds = new HashMap<>();
        if (subbracket1 != this) {
            Map<String, Integer> subbracketSeeds = subbracket1.getSeeds();
            for (String team : subbracketSeeds.keySet()) {
                seeds.put(team, subbracketSeeds.get(team));
            }
        } else if (!team1.equals("BYE")) {
            seeds.put(team1, seed1);
        }
        if (subbracket2 != this) {
            Map<String, Integer> subbracketSeeds = subbracket2.getSeeds();
            for (String team : subbracketSeeds.keySet()) {
                seeds.put(team, subbracketSeeds.get(team));
            }
        } else if (!team2.equals("BYE")) {
            seeds.put(team2, seed2);
        }
        return seeds;
    }

}
