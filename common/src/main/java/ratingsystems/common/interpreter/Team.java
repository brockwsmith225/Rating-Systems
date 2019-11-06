package ratingsystems.common.interpreter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ratingsystems.common.linalg.Vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Team implements Comparable<Team>, Serializable {

    protected String name, conference, coach;
    protected int year;
    protected double rating;
    protected int numberOfGames;
    protected List<Game> games;

    public Team() {
        this.name = "";
        this.conference = "";
        this.coach = "";
        this.year = 0;
        this.rating = 0.0;
        this.numberOfGames = 0;
        this.games = new ArrayList<>();
    }

    /**
     * Creates a new instance of a team.
     *
     * @param name the name of the new team
     */
    public Team(String name, String conference, String coach, int year) {
        this.name = name;
        this.conference = conference;
        this.coach = coach;
        this.year = year;
        this.rating = 0.0;
        this.numberOfGames = 0;
        this.games = new ArrayList<>();
    }

    /**
     * Returns the name of the team
     *
     * @return the name of the team
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the conference the team belongs to
     *
     * @return the conference the team belongs to
     */
    public String getConference() {
        return conference;
    }

    public String getCoach() {
        return coach;
    }

    public int getYear() {
        return year;
    }

    /**
     * Sets the rating of the team
     *
     * @param rating the new rating of the team
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Returns the rating of the team
     *
     * @return the rating of the team
     */
    public double getRating() {
        return rating;
    }

    /**
     * Adds a game to the team
     *
     * @param game the game to be added
     */
    public void addGame(Game game) {
        games.add(game);
        numberOfGames++;
    }

    public void addDefensiveStats(Game opponentGame) {
        for (Game game : games) {
            if (game.equalsReversed(opponentGame)) {
                game.addDefensiveStatistics(opponentGame);
            }
        }
    }

    /**
     * Returns the number of games the team played
     *
     * @return the number of games the team played
     */
    public int getNumberOfGames() {
        return numberOfGames;
    }

    /**
     * Returns a copy of the games of the team
     *
     * @return a copy of the games of the team
     */
    @JsonIgnore
    public List<Game> getGames() {
        List<Game> dataCopy = new ArrayList<>();
        for (Game game : games) {
            dataCopy.add(Game.copyOf(game));
        }
        return dataCopy;
    }

    public void updateGame(Game oldGame, Game newGame) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).equals(oldGame)) {
                games.set(i, newGame);
            }
        }
    }

    public int getNumberOfWins() {
        int wins = 0;
        for (Game game : games) {
            if (game.getScore() > game.getOpponentScore()) wins++;
        }
        return wins;
    }

    public int getNumberOfLosses() {
        int losses = 0;
        for (Game game : games) {
            if (game.getScore() < game.getOpponentScore()) losses++;
        }
        return losses;
    }

    public String getRecord() {
        return getNumberOfWins() + "-" + getNumberOfLosses();
    }

    /**
     * Calculates the average points scored by the team per game
     *
     * @return the average points scored per game
     */
    public double getPointsPerGame() {
        double pointsPerGame = 0.0;
        for (Game game : games) {
            pointsPerGame += game.getScore();
        }
        return pointsPerGame / numberOfGames;
    }

    /**
     * Calculates the average points allowed by the team per game
     *
     * @return the average points allowed per game
     */
    public double getPointsAllowedPerGame() {
        double pointsAllowedPerGame = 0.0;
        for (Game game : games) {
            pointsAllowedPerGame += game.getOpponentScore();
        }
        return pointsAllowedPerGame / numberOfGames;
    }

    public double getStatisticPerGame(String statistic) {
        double statisticPerGame = 0.0;
        for (Game game : games) {
            statisticPerGame += game.getStatistic(statistic);
        }
        return statisticPerGame / numberOfGames;
    }

    @JsonIgnore
    public Vector getStatisticsVector() {
        if (games.size() == 0) {
            return null;
        }

        Vector vector = games.get(0).getStatisticsVector();
        for (int i = 1; i < games.size(); i++) {
            vector = vector.add(games.get(0).getStatisticsVector());
        }
        vector = vector.replaceZeroes(0.0000000001);
        return vector.multiply(1.0 / games.size());
    }

    public Vector getStatisticsVector(Map<String, Vector> teamVectors) {
        if (games.size() == 0) {
            return null;
        }

        Vector vector = games.get(0).getStatisticsVector();
        for (int i = 1; i < games.size(); i++) {
            vector = vector.add(games.get(i).getStatisticsVector().piecewiseMultiplication(teamVectors.get(games.get(i).getOpponent()).multiplicativeInverse()));
        }
        return vector.multiply(1.0 / games.size());
    }

    @Override
    public int compareTo(Team e) {
        return Double.compare(e.getRating(), rating);
    }

    @Override
    public String toString() {
        return name;
    }

    public static Team copyOf(Team team) {
        Team copy = new Team(team.name, team.conference, team.coach, team.year);
        copy.rating = team.rating;
        copy.numberOfGames = team.numberOfGames;
        for (Game game : team.games) {
            copy.games.add(Game.copyOf(game));
        }
        return copy;
    }
}
