package ratingsystems.common.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;

public abstract class Interpreter {

    protected HashMap<String, Team> teams;
    protected HashSet<String> addedTeams;
    protected ArrayList<String> groups;
    protected HashSet<String> addedGroups;


    /**
     * Handles the setup for the necessary instance variables which need to be created new each
     * time the interpreter parses data, rather than when the interpreter is created
     */
    public void setup() {
        teams = new HashMap<>();
        groups = new ArrayList<>();
        addedTeams = new HashSet<>();
        addedGroups = new HashSet<>();
    }

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param year the year of the data
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public HashMap<String, Team> parseData(int year) throws FileNotFoundException;

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param year the year of the data
     * @param week the maximum week of the data to be included
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public HashMap<String, Team> parseData(int year, int week) throws FileNotFoundException;

    /**
     * Interprets the data found in the data files specified by the file paths
     *
     * @param years the years of the data
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if any of the data files specified by the file paths is not found
     */
    abstract public HashMap<String, Team> parseData(int[] years, boolean cumulative) throws FileNotFoundException;

    /**
     * Interprets the data found in the data files specified by the file paths
     *
     * @param years the years of the data
     * @param week the maximum week of the data to be included for the last year in years
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if any of the data files specified by the file paths is not found
     */
    abstract public HashMap<String, Team> parseData(int[] years, int week, boolean cumulative) throws FileNotFoundException;

    /**
     * Adds the team to the interpreter results if it has not already been added
     *
     * @param team the team to be added
     * @param conference the conference of the team
     */
    protected void addTeam(String team, String conference, int year) {
        if (addedTeams.add(team)) {
            teams.put(team, new Team(team));
            teams.get(team).setConference(conference);
            teams.get(team).setYear(year);
        }

        if (addedGroups.add(conference)) {
            groups.add(conference);
        }
    }

    /**
     * Returns the groups of the teams
     *
     * @return the groups
     */
    public ArrayList<String> groups() {
        return new ArrayList<>(groups);
    }

    /**
     * Checks to see if the data file specified by the year
     *
     * @param year the year of the data
     * @return true if the file exists, false otherwise
     */
    abstract public boolean hasData(int year);

    /**
     * Gets data from the data file specified by the year
     *
     * @param year the year of the data
     * @return a scanner with the data loaded
     */
    abstract public Scanner getData(int year) throws FileNotFoundException;

    abstract public void fetchData(int year) throws IOException;

    protected void addDefensiveStatistics() {
        for (Team team : teams.values()) {
            ArrayList<Game> games = team.getGames();
            for (Game game : games) {
                teams.get(game.getOpponent()).addDefensiveStats(game);
            }
        }
    }

    /**
     * Splits the inputted string by the inputted delimiter. Ignores
     * portions of the inputted string that are within quotes
     *
     * @param input the input to be split
     * @return the split input
     */
    public static String[] split(String input, String delimiter) {
        String[] res = input.split(delimiter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replace("\"", "");
        }
        return res;
    }
}