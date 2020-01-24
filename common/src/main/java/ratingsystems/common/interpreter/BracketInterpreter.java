package ratingsystems.common.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BracketInterpreter {
    public Bracket getBracket(String file) throws FileNotFoundException {
        File source = new File(file);
        if (source.exists() && source.isFile()) {
            Scanner data = new Scanner(source);

            List<Bracket> brackets = new ArrayList<>();
            String team1 = "";
            String team2 = "";
            int seed1 = -1;
            int seed2 = -1;
            Bracket subbracket1 = null;
            Bracket subbracket2 = null;
            boolean side1 = false;
            boolean side2 = false;
            String bracketName = "";
            List<Location> location = new ArrayList<>();
            location.add(Location.NEUTRAL);
            while (data.hasNext()) {
                String line = data.nextLine();
                if (line.startsWith("`")) {
                    line = line.substring(1);
                    location.clear();
                    String[] locations = line.split(",");
                    for (String loc : locations) {
                        if (loc.equals("H")) {
                            location.add(Location.HOME);
                        } else if (loc.equals("A")) {
                            location.add(Location.AWAY);
                        } else {
                            location.add(Location.NEUTRAL);
                        }
                    }
                } else if (line.startsWith("--")) {
                    bracketName = line.substring(2);
                } else if (line.startsWith("*")) {
                    line = line.substring(1);
                    int seed = -1;
                    if (line.startsWith("!")) {
                        seed = Integer.parseInt(line.split("\\!")[1]);
                        line = line.split("\\!")[2];
                    }
                    String t1 = line.split("\\/")[0];
                    String t2 = line.split("\\/")[1];
                    if (side1) {
                        subbracket2 = new Bracket(t1, seed, t2, seed, location.get(0), bracketName);
                        seed2 = seed;
                        side2 = true;
                    } else {
                        subbracket1 = new Bracket(t1, seed, t2, seed, location.get(0), bracketName);
                        seed1 = seed;
                        side1 = true;
                    }
                } else {
                    int seed = -1;
                    if (line.startsWith("!")) {
                        seed = Integer.parseInt(line.split("\\!")[1]);
                        line = line.split("\\!")[2];
                    }
                    if (side1) {
                        team2 = line;
                        seed2 = seed;
                        side2 = true;
                    } else {
                        team1 = line;
                        seed1 = seed;
                        side1 = true;
                    }
                }
                if (side1 && side2) {
                    Bracket bracket;
                    if (team1.length() > 0 && team2.length() > 0) {
                        bracket = new Bracket(team1, seed1, team2, seed2, location.get(0), bracketName);
                    } else if (team1.length() > 0) {
                        bracket = new Bracket(team1, seed1, subbracket2, location.get(0), bracketName);
                    } else if (team2.length() > 0) {
                        bracket = new Bracket(subbracket1, team2, seed2, location.get(0), bracketName);
                    } else {
                        bracket = new Bracket(subbracket1, subbracket2, location.get(0), bracketName);
                    }
                    brackets.add(bracket);
                    team1 = "";
                    team2 = "";
                    seed1 = -1;
                    seed2 = -1;
                    subbracket1 = null;
                    subbracket2 = null;
                    side1 = false;
                    side2 = false;
                }
            }
            int round = 1;
            for (int i = 0; brackets.size() > 1; i++) {
                subbracket1 = brackets.get(i);
                subbracket2 = brackets.get(i+1);
                Bracket bracket = new Bracket(subbracket1, subbracket2, location.get(Math.min(round, location.size()-1)));
                brackets.remove(i);
                brackets.remove(i);
                brackets.add(i, bracket);
                if (i >= brackets.size())
                    i = 0;
            }
            return brackets.get(0);
        }
        return null;
    }
}
