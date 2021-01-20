package ratingsystems.common.ratingsystem;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.interpreter.Team;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public abstract class RatingSystem {
    protected Map<String, Team> teams;
    protected ArrayList<Team> rankedTeams;
    protected Interpreter interpreter;
    protected int year, week;
    protected boolean cumulative;

    /**
     * Creates a new instance of a Rating System with no data
     */
    public RatingSystem() {
        teams = new HashMap<>();
        rankedTeams = new ArrayList<>();
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
        this.cumulative = true;
        this.teams = interpreter.parseData(year);
        this.rankedTeams = new ArrayList<>();
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
        this.cumulative = true;
        this.teams = interpreter.parseData(year, week);
        this.rankedTeams = new ArrayList<>();
        this.interpreter = interpreter;
    }

    public RatingSystem(Interpreter interpreter, int[] years, boolean cumulative) throws FileNotFoundException {
        this.year = years[years.length - 1];
        this.week = -1;
        this.cumulative = cumulative;
        this.teams = interpreter.parseData(years, cumulative);
        this.rankedTeams = new ArrayList<>();
        this.interpreter = interpreter;
    }

    public RatingSystem(Interpreter interpreter, int[] years, int week, boolean cumulative) throws FileNotFoundException {
        this.year = years[years.length - 1];
        this.week = week;
        this.cumulative = cumulative;
        this.teams = interpreter.parseData(years, week, cumulative);
        this.rankedTeams = new ArrayList<>();
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
                rankings.append(prettyPrintTeam(rankedTeams.get(i).getName() + (!cumulative ? "-" + rankedTeams.get(i).getYear() : ""), allStats));
                rankings.append("\n");
            } else {
                rankings.append(rank);
                rankings.append("\t");
                rankings.append(printTeam(rankedTeams.get(i).getName() + (!cumulative ? "-" + rankedTeams.get(i).getYear() : ""), allStats));
                rankings.append("\n");
            }
        }
        return rankings.toString();
    }

    public String printTeamStandings(boolean prettyPrint, boolean allStats) {
        Set<String> conferences = new HashSet<>();
        for (String team : teams.keySet()) {
            conferences.add(teams.get(team).getConference());
        }
        StringBuilder standings = new StringBuilder();
        for (String conference : conferences) {
            if (prettyPrint) {
                standings.append(prettyPrintStandingsHeader(conference, allStats));
                standings.append("\n");
                standings.append(prettyPrintColumnHeaders(allStats));
                standings.append("\n");
            } else {
                standings.append(printStandingsHeader(conference));
                standings.append("\n");
            }
            int rank = 1;
            int standing = 1;
            int s = 0;
            for (int i = 0; i < rankedTeams.size(); i++) {
                if (i > 0 && rankedTeams.get(i).getRating() != rankedTeams.get(i - 1).getRating()) {
                    rank = i + 1;
                    standing = s + 1;
                }
                if (rankedTeams.get(i).getConference().equals(conference)) {
                    if (prettyPrint) {
                        standings.append(Terminal.rightJustify(standing + " (" + rank + ")", 8));
                        standings.append(". ");
                        standings.append(prettyPrintTeam(rankedTeams.get(i).getName() + (!cumulative ? "-" + rankedTeams.get(i).getYear() : ""), allStats));
                        standings.append("\n");
                    } else {
                        standings.append(standing);
                        standings.append("\t");
                        standings.append(rank);
                        standings.append("\t");
                        standings.append(printTeam(rankedTeams.get(i).getName() + (!cumulative ? "-" + rankedTeams.get(i).getYear() : ""), allStats));
                        standings.append("\n");
                    }
                    s++;
                }
            }
            standings.append("\n");
        }
        return standings.toString();
    }

    public List<Team> getTeamRankings() {
        return List.copyOf(rankedTeams);
    }

    public int getYear() {
        return year;
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
                if (game.getWeek() == week && (game.getLocation() == Location.HOME || (game.getLocation() == Location.NEUTRAL && game.getTeam().compareTo(game.getOpponent()) < 0))) {
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
            error += prediction + game.getScoreDiff();
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
            header.append(" ");
            header.append(year);
        } else {
            header.append(" ");
            header.append(year);
            header.append(" Week ");
            header.append(week);
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
     * Returns the header to be printed when printing team rankings
     *
     * @return the header to be printed when printing team rankings
     */
    protected String printStandingsHeader(String conference) {
        StringBuilder header = new StringBuilder();
        header.append("---------------\n");
        if (week < 0) {
            header.append(" ");
            header.append(conference);
            header.append(" ");
            header.append(year);
        } else {
            header.append(" ");
            header.append(conference);
            header.append(" ");
            header.append(year);
            header.append(" Week ");
            header.append(week);
        }
        header.append("\n---------------\n");
        return header.toString();
    }

    /**
     * Returns a pretty print version of the header to be printed when printing team rankings
     *
     * @return a pretty print version of the header to be printed when printing team rankings
     */
    protected String prettyPrintStandingsHeader(String conference, boolean allStats) {
        StringBuilder header = new StringBuilder();
        header.append("----------------------------------------------------------------------------------\n");
        if (week < 0) {
            header.append(Terminal.centerJustify(conference + " " + Integer.toString(year), 82));
        } else {
            header.append(Terminal.centerJustify(conference + " " + year + " Week " + week, 82));
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
        header.append("     ");
        header.append(Terminal.leftJustify("Team", 50));
        header.append("   ");
        header.append(!cumulative ? "Year" : "");
        header.append("   ");
        header.append(Terminal.leftJustify("Rating", 10));
        header.append("   ");
        header.append(Terminal.leftJustify("Record", 10));
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
                + (!cumulative ? teams.get(team).getYear() + "\t" : "")
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
                + (!cumulative ? teams.get(team).getYear() + "   " : "")
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

    public String printPredictions(List<Game> games, boolean prettyPrint) {
        StringBuilder predictions = new StringBuilder();
        for (Game game : games) {
            Prediction prediction = predictGame(game.getTeam(), game.getOpponent(), game.getLocation());
            predictions.append(game.getWeek());
            predictions.append("\t");
            predictions.append(game.getDate());
            predictions.append("\t");
            predictions.append(game.getTeam());
            predictions.append("\t");
            predictions.append(game.getOpponent());
            predictions.append("\t");
            predictions.append((game.getLocation() == Location.NEUTRAL ? "N" : ""));
            predictions.append("\t");
            predictions.append(prediction.getTeam1Score());
            predictions.append("\t");
            predictions.append(prediction.getTeam2Score());
            predictions.append("\t");
            predictions.append(prediction.getOdds());
            predictions.append("\t");
            predictions.append(prediction.getLine());
            predictions.append("\t");
            predictions.append(prediction.getOverUnder());
            predictions.append("\t");
            predictions.append(game.getScore());
            predictions.append("\t");
            predictions.append(game.getOpponentScore());
            predictions.append("\t");
            predictions.append((-1 * game.getScoreDiff()));
            predictions.append("\t");
            predictions.append((game.getScore() + game.getOpponentScore()));
            predictions.append("\n");
        }
        predictions = predictions.deleteCharAt(predictions.length() - 1);
        return predictions.toString();
    }

    abstract public boolean hasTeam(String team);

    public Team getTeam(String team) {
        if (teams.containsKey(team)) {
            return teams.get(team);
        }
        return null;
    }

    public void predictPlayoff() {

    }
}
