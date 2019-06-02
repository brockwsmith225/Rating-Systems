package ratingSystem;

import interpreter.Interpreter;
import interpreter.datatypes.Team;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class RatingSystem {
    protected HashMap<String, Team> teams;
    protected ArrayList<Team> rankedTeams;
    protected ArrayList<Team> rankedGroups;
    protected Interpreter interpreter;

    /**
     * Creates a new instance of a Rating System with no data
     */
    public RatingSystem() {
        teams = new HashMap<>();
        rankedTeams = new ArrayList<>();
        rankedGroups = new ArrayList<>();
    }

    /**
     * Creates a new instance of a Rating System with the data found in the file specified by the file path
     *
     * @param interpreter the interpreter to be used to read in the data from the data file
     * @param year the year of the data
     * @throws FileNotFoundException if the file specified by the file path is not found
     */
    public RatingSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        teams = interpreter.parseData(year);
        rankedTeams = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }

    /**
     * Creates a new instance of a Rating System with the data found in the file specified by the file path
     *
     * @param interpreter the interpreter to be used to read in the data from the data file
     * @param year the year of the data
     * @param week the maximum week of the data to be included
     * @throws FileNotFoundException if the file specified by the file path is not found
     */
    public RatingSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        teams = interpreter.parseData(year, week);
        rankedTeams = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }

    /**
     * Setups the method for rating the teams as necessary
     */
    abstract public void setup();

    /**
     * Ranks the teams
     */
    public void rankTeams() {
        rankedTeams = new ArrayList<>(teams.values());
        Collections.sort(rankedTeams);
    }

    /**
     * Ranks the groups of teams
     */
    abstract public void rankGroups();

    /**
     * Prints the teams in ranked order along with their ratings
     */
    public void printTeamRankings() {
        int rank = 1;
        for (int i = 0; i < rankedTeams.size(); i++) {
            if (i > 0 && rankedTeams.get(i).getRating() != rankedTeams.get(i - 1).getRating()) {
                rank = i + 1;
            }
            System.out.println(rank + ". " + rankedTeams.get(i).getName() + " " + rankedTeams.get(i).getRating());
        }
    }
}
