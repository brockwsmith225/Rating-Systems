package ratingsystems.common.ratingsystem;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.interpreter.Team;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class RatingSystem {
    protected HashMap<String, Team> teams;
    protected ArrayList<Team> rankedTeams;
    protected ArrayList<Team> rankedGroups;
    protected Interpreter interpreter;
    protected int year, week;

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
        this.year = year;
        this.week = -1;
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
        this.year = year;
        this.week = week;
        teams = interpreter.parseData(year, week);
        rankedTeams = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }

    public RatingSystem(Interpreter interpreter, int[] years) throws FileNotFoundException {
        this.year = years[years.length - 1];
        teams = interpreter.parseData(years);
        rankedTeams = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }

    public RatingSystem(Interpreter interpreter, int[] years, int week) throws FileNotFoundException {
        this.year = years[years.length - 1];
        this.week = week;
        teams = interpreter.parseData(years, week);
        rankedTeams = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }

    /**
     * Sets up the method for rating the teams as necessary
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
     *
     * @param prettyPrint denotes whether or not to format the output
     */
    public String printTeamRankings(boolean prettyPrint, boolean allStats) {
        StringBuilder rankings = new StringBuilder();
        if (prettyPrint) {
            rankings.append(prettyPrintRankingsHeader(allStats));
            rankings.append("\n");
            rankings.append(prettyPrintColumnHeaders(allStats));
            rankings.append("\n");
        } else {
            rankings.append(printRankingsHeader());
            rankings.append("\n");
        }
        int rank = 1;
        for (int i = 0; i < rankedTeams.size(); i++) {
            if (i > 0 && rankedTeams.get(i).getRating() != rankedTeams.get(i - 1).getRating()) {
                rank = i + 1;
            }
            if (prettyPrint) {
                rankings.append(Terminal.rightJustify(Integer.toString(rank), 3));
                rankings.append(". ");
                rankings.append(prettyPrintTeam(rankedTeams.get(i).getName(), allStats));
                rankings.append("\n");
            } else {
                rankings.append(rank);
                rankings.append("\t");
                rankings.append(printTeam(rankedTeams.get(i).getName(), allStats));
                rankings.append("\n");
            }
        }
        return rankings.toString();
    }

    public List<Team> getTeamRankings() {
        return List.copyOf(rankedTeams);
    }

    /**
     * Returns the week of the most recent game
     *
     * @return the week of the most recent game
     */
    public int getWeek() {
        int week = 0;
        for (Team team : teams.values()) {
            for (Game game : team.getGames()) {
                if (game.getWeek() > week) {
                    week = game.getWeek();
                }
            }
        }
        return week;
    }

    /**
     * Returns a list of the games in a given week
     *
     * @param week a week of the season
     * @return a list of the games in the given week
     */
    public List<Game> getGames(int week) {
        List<Game> games = new ArrayList<>();
        for (Team team : teams.values()) {
            for (Game game : team.getGames()) {
                if (game.getWeek() == week) {
                    games.add(game);
                }
            }
        }
        return games;
    }

    public double checkPreditions(List<Game> games) {
        double correct = 0;
        for (Game game : games) {
            double prediction = predictGame(game.getTeam(), game.getOpponent(), game.getLocation()).getOdds();
            if (game.getScore() > game.getOpponentScore() && prediction > 0.5) correct++;
            if (game.getScore() < game.getOpponentScore() && prediction < 0.5) correct++;
            if (prediction == 0.5) correct += 0.5;
        }
        return correct;
    }

    public double checkError(List<Game> games) {
        double error = 0.0;
        for (Game game : games) {
            double prediction = predictGame(game.getTeam(), game.getOpponent(), game.getLocation()).getLine();
            error += Math.abs(prediction) - Math.abs(game.getScoreDiff());
        }
        return error;
    }

    public double checkAbsoluteError(List<Game> games) {
        double error = 0.0;
        for (Game game : games) {
            double prediction = -1 * predictGame(game.getTeam(), game.getOpponent(), game.getLocation()).getLine();
            error += Math.abs(prediction - game.getScoreDiff());
        }
        return error;
    }

    public LocalDate getStartOfWeek(int week) {
        LocalDate startDate = LocalDate.of(year, 12, 31);
        for (Team team : teams.values()) {
            for (Game game : team.getGames()) {
                if (game.getDate().compareTo(startDate) < 0) {
                    startDate = game.getDate().plusDays(0);
                }
            }
        }
        while (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            startDate = startDate.minusDays(1);
        }
        startDate = startDate.plusDays((week - 1) * 7);
        return startDate;
    }

    public LocalDate getEndOfWeek(int week) {
        LocalDate endDate = getStartOfWeek(week);
        endDate = endDate.plusDays(6);
        return endDate;
    }

    /**
     * Returns the header to be printed when printing team rankings
     *
     * @return the header to be printed when printing team rankings
     */
    protected String printRankingsHeader() {
        StringBuilder header = new StringBuilder();
        header.append("---------------\n");
        if (week < 0) {
            header.append(" " + year);
        } else {
            header.append(" " + year + " Week " + week);
        }
        header.append("\n---------------\n");
        return header.toString();
    }

    /**
     * Returns a pretty print version of the header to be printed when printing team rankings
     *
     * @return a pretty print version of the header to be printed when printing team rankings
     */
    protected String prettyPrintRankingsHeader(boolean allStats) {
        StringBuilder header = new StringBuilder();
        header.append("----------------------------------------------------------------------------------\n");
        if (week < 0) {
            header.append(Terminal.centerJustify(Integer.toString(year), 82));
        } else {
            header.append(Terminal.centerJustify(year + " Week " + week, 82));
        }
        header.append("\n----------------------------------------------------------------------------------");
        return header.toString();
    }

    /**
     * Returns a pretty print version of the column headers to be printed when printing team rankings
     *
     * @return a pretty print version of the column headers to be printed when printing team rankings
     */
    protected String prettyPrintColumnHeaders(boolean allStats) {
        StringBuilder header = new StringBuilder();
        header.append("     " + Terminal.leftJustify("Team", 50) + "   " + Terminal.leftJustify("Rating", 10) + "   " + Terminal.leftJustify("Record", 10));
        return header.toString();
    }


    /**
     * Returns the team and stats to be printed when printing team rankings
     *
     * @param team the team to be printed
     * @return a string formatted to print the given team
     */
    protected String printTeam(String team, boolean allStats) {
        return teams.get(team).getName() + "\t"
                + teams.get(team).getRating() + "\t"
                + teams.get(team).getRecord();
    }

    /**
     * Returns a pretty print version of the team and stats to be printed when printing team rankings
     *
     * @param team the team to be printed
     * @return a a pretty print string formatted to print the given team
     */
    protected String prettyPrintTeam(String team, boolean allStats) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getRating()), 10) + "   "
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
    }

    /**
     * Calculates the odds of a team winning in a given game
     *
     * @param team1 the first team in the game
     * @param team2 the second team in the game
     * @param location the location of the game ('H', 'A', 'N')
     * @return the odds that team 1 wins
     */
    abstract public Prediction predictGame(String team1, String team2, Location location);

    public boolean hasTeam(String team) {
        return teams.containsKey(team);
    }

    public Team getTeam(String team) {
        if (teams.containsKey(team)) {
            return teams.get(team);
        }
        return null;
    }
}
