package interpreter.datatypes;

import java.util.HashMap;

/**
 * A representation of a game which compares two teams.
 */
public class Game {
    private String otherTeam;
    private double score, otherScore, weightedScoreDiff;
    private Time time;
    private HashMap<String, Double> otherData;

    /**
     *  Creates a new game.
     *
     * @param otherTeam the name of the other team of the game
     * @param score the score of the team the game belongs to
     * @param otherScore the score of the other team of the game
     * @param weightedScoreDiff the weighted difference of scores
     * @param time the date at which the game occurred
     */
    public Game(String otherTeam, double score, double otherScore, double weightedScoreDiff, Time time) {
        this.otherTeam = otherTeam;
        this.score = score;
        this.otherScore = otherScore;
        this.weightedScoreDiff = weightedScoreDiff;
        this.time = time;
    }

    /**
     * Returns the name of the other team of the game
     *
     * @return the name of the other team of the game
     */
    public String getOtherTeam() {
        return otherTeam;
    }

    /**
     * Returns the score of the team the game belongs to
     *
     * @return the score of the team the game belongs to
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns the score of the other team of the game
     *
     * @return the score of the other team of the game
     */
    public double getOtherScore() {
        return otherScore;
    }

    /**
     * Returns the weighted difference of scores
     *
     * @return the weighted difference of scores
     */
    public double getWeightedScoreDiff() {
        return weightedScoreDiff;
    }

    /**
     * Returns the time at which the game occurred
     *
     * @return the time at which the game occurred
     */
    public Time getTime() {
        return time;
    }

    /**
     * Returns a copy of the game
     *
     * @param game the game to be copied
     *
     * @return a copy of the game
     */
    static Game copyOf(Game game) {
        return new Game(game.otherTeam, game.score, game.otherScore, game.weightedScoreDiff, game.time);
    }
}
