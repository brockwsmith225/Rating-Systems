package ratingsystems.common.interpreter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ratingsystems.common.linalg.Vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class Team implements Comparable<Team>, Serializable {

    private String name, conference;
    private int year;
    private double rating;
    private HashMap<String, Double> otherRatings;
    private int numberOfGames;
    private ArrayList<Game> games;

    /**
     * Creates a new instance of a team.
     *
     * @param name the name of the new team
     */
    public Team(String name) {
        this.name = name;
        this.conference = "";
        this.year = 0;
        this.rating = 0.0;
        this.otherRatings = new HashMap<>();
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
     * Assigns the team to a particular conference
     *
     * @param conference the name of the conference to assign the team to
     */
    public void setConference(String conference) {
        this.conference = conference;
    }

    /**
     * Returns the conference the team belongs to
     *
     * @return the conference the team belongs to
     */
    public String getConference() {
        return conference;
    }

    public void setYear(int year) {
        this.year = year;
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
     * Sets the rating of the team
     *
     * @param rating the new rating of the team
     */
    public void setRating(String ratingName, double rating) {
        otherRatings.put(ratingName, rating);
    }

    /**
     * Returns the rating of the team
     *
     * @return the rating of the team
     * @throws NullPointerException if the rating requested does not exist
     */
    public double getRating(String ratingName) throws NullPointerException {
        if (!otherRatings.containsKey(ratingName)) {
            throw new NullPointerException("Rating " + ratingName + " not found.");
        }
        return otherRatings.get(ratingName);
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
    public ArrayList<Game> getGames() {
        ArrayList<Game> dataCopy = new ArrayList<>();
        for (Game game : games) {
            dataCopy.add(Game.copyOf(game));
        }
        return dataCopy;
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

    public Vector getStatisticsVector(HashMap<String, Vector> teamVectors) {
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
        return Double.compare(e.rating, rating);
    }

    @Override
    public String toString() {
        return name;
    }

    public static Team copyOf(Team team) {
        Team copy = new Team(team.name);
        copy.conference = team.conference;
        copy.rating = team.rating;
        for (String rating : team.otherRatings.keySet()) {
            copy.otherRatings.put(rating, team.otherRatings.get(rating));
        }
        copy.numberOfGames = team.numberOfGames;
        for (Game game : team.games) {
            copy.games.add(Game.copyOf(game));
        }
        return copy;
    }
}