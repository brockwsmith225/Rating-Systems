package ratingsystems.common.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public abstract class Interpreter {

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param year the year of the data
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public Map<String, Team> parseData(int year) throws FileNotFoundException;

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param year the year of the data
     * @param week the maximum week of the data to be included
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public Map<String, Team> parseData(int year, int week) throws FileNotFoundException;

    /**
     * Interprets the data found in the data files specified by the file paths
     *
     * @param years the years of the data
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if any of the data files specified by the file paths is not found
     */
    public Map<String, Team> parseData(int[] years, boolean cumulative) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        for (int year : years) {
            if (cumulative) {
                Map<String, Team> partialTeams = parseData(year);
                for (String team : partialTeams.keySet()) {
                    if (!teams.containsKey(team)) {
                        teams.put(team, partialTeams.get(team));
                    } else {
                        for (Game game : partialTeams.get(team).getGames()) {
                            teams.get(team).addGame(game);
                        }
                    }
                }
            } else {
                Map<String, Team> partialTeams = parseData(year);
                for (String team : partialTeams.keySet()) {
                    for (Game oldGame : partialTeams.get(team).getGames()) {
                        Game newGame = new Game(oldGame.getTeam(), oldGame.getOpponent() + "-" + year, oldGame.getLocation(), oldGame.getScore(), oldGame.getOpponentScore(), oldGame.getWeightedScoreDiff(), oldGame.getWeek(), oldGame.getDate(), oldGame.getStatistics());
                        partialTeams.get(team).updateGame(oldGame, newGame);
                    }
                    teams.put(team + "-" + year, partialTeams.get(team));
                }
            }
        }

        return teams;
    }

    /**
     * Interprets the data found in the data files specified by the file paths
     *
     * @param years the years of the data
     * @param week the maximum week of the data to be included for the last year in years
     * @return a collection of the teams found in the data file
     * @throws FileNotFoundException if any of the data files specified by the file paths is not found
     */
    public Map<String, Team> parseData(int[] years, int week, boolean cumulative) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        for (int year : years) {
            if (cumulative) {
                Map<String, Team> partialTeams;
                if (year == years[years.length - 1]) {
                    partialTeams = parseData(year, week);
                } else {
                    partialTeams = parseData(year);
                }
                for (String team : partialTeams.keySet()) {
                    if (!teams.containsKey(team)) {
                        teams.put(team, partialTeams.get(team));
                    } else {
                        for (Game game : partialTeams.get(team).getGames()) {
                            teams.get(team).addGame(game);
                        }
                    }
                }
            } else {
                Map<String, Team> partialTeams = parseData(year, week);
                for (String team : partialTeams.keySet()) {
                    for (Game oldGame : partialTeams.get(team).getGames()) {
                        Game newGame = new Game(oldGame.getTeam(), oldGame.getOpponent() + "-" + year, oldGame.getLocation(), oldGame.getScore(), oldGame.getOpponentScore(), oldGame.getWeightedScoreDiff(), oldGame.getWeek(), oldGame.getDate(), oldGame.getStatistics());
                        partialTeams.get(team).updateGame(oldGame, newGame);
                    }
                    teams.put(team + "-" + year, partialTeams.get(team));
                }
            }
        }

        return teams;
    }

    /**
     * Checks to see if the data file specified by the year exists
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

    /**
     * Checks to see if the bracket file specified by the year exists
     *
     * @param year the year of the bracket
     * @return true if the file exists, false otherwise
     */
    abstract public boolean hasBracket(int year);

    /**
     * Gets bracket from the bracket file specified by the year
     *
     * @param year the year of the bracket
     * @return a bracket object loaded with the data
     */
    abstract public Bracket parseBracket(int year) throws FileNotFoundException;

    abstract public void fetchData(int year) throws IOException;

    protected void addDefensiveStatistics(Map<String, Team> teams) {
        for (String team : teams.keySet()) {
            List<Game> games = teams.get(team).getGames();
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
