package interpreter.datatypes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class Team implements Comparable<Team> {

    private String name, group;
    private double rating;
    private HashMap<String, Double> otherRatings;
    private int numberOfGames;
    private ArrayList<Game> games;

    /**
     * Creates a new instance of an team.
     *
     * @param name the name of the new team
     */
    public Team(String name) {
        this.name = name;
        this.group = "";
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
     * Assigns the team to a particular group
     *
     * @param group the name of the group to assign the team to
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Returns the group the team belongs to
     *
     * @return the group the team belongs to
     */
    public String getGroup() {
        return group;
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

    /**
     * Returns a copy of the games of the team
     *
     * @return a copy of the games of the team
     */
    public ArrayList<Game> getGames() {
        ArrayList<Game> dataCopy = new ArrayList<>();
        for (Game dp : games) {
            dataCopy.add(Game.copyOf(dp));
        }
        return dataCopy;
    }

    @Override
    public int compareTo(Team e) {
        if (rating < e.rating) {
            return 1;
        }
        if (rating > e.rating) {
            return -1;
        }
        return 0;
    }
}
