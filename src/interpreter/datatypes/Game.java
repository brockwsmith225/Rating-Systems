package interpreter.datatypes;

import java.util.HashMap;

/**
 * A representation of a game which compares two teams.
 */
public class Game {
    private String opponent;
    private double score, opponentScore, weightedScoreDiff;
    private Time time;
    private HashMap<String, Double> otherData;

    /**
     *  Creates a new game.
     *
     * @param opponent the name of the opponent of the game
     * @param score the score of the team the game belongs to
     * @param opponentScore the score of the opponent of the game
     * @param weightedScoreDiff the weighted difference of scores
     * @param time the date at which the game occurred
     */
    public Game(String opponent, double score, double opponentScore, double weightedScoreDiff, Time time) {
        this.opponent = opponent;
        this.score = score;
        this.opponentScore = opponentScore;
        this.weightedScoreDiff = weightedScoreDiff;
        this.time = time;
    }

    /**
     * Returns the name of the opponent of the game
     *
     * @return the name of the opponent of the game
     */
    public String getOpponent() {
        return opponent;
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
     * Returns the score of the opponent of the game
     *
     * @return the score of the opponent of the game
     */
    public double getOpponentScore() {
        return opponentScore;
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
        return new Game(game.opponent, game.score, game.opponentScore, game.weightedScoreDiff, game.time);
    }
}
