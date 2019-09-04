package ratingsystems.common.interpreter;

import ratingsystems.common.linalg.Vector;

import java.time.LocalDate;
import java.util.HashMap;

/**
 * A representation of a game which compares two teams.
 */
public class Game {
    private String team, opponent;
    private double score, opponentScore, weightedScoreDiff;
    private int week;
    private LocalDate date;
    private HashMap<String, Double> statistics;

    /**
     *  Creates a new game.
     *
     * @param opponent the name of the opponent of the game
     * @param score the score of the team the game belongs to
     * @param opponentScore the score of the opponent of the game
     * @param weightedScoreDiff the weighted difference of scores
     * @param date the date at which the game occurred
     */
    public Game(String team, String opponent, double score, double opponentScore, double weightedScoreDiff, int week, LocalDate date) {
        this.team = team;
        this.opponent = opponent;
        this.score = score;
        this.opponentScore = opponentScore;
        this.weightedScoreDiff = weightedScoreDiff;
        this.week = week;
        this.date = date;
        this.statistics= new HashMap<>();
    }

    /**
     *  Creates a new game.
     *
     * @param opponent the name of the opponent of the game
     * @param score the score of the team the game belongs to
     * @param opponentScore the score of the opponent of the game
     * @param weightedScoreDiff the weighted difference of scores
     * @param date the date at which the game occurred
     */
    public Game(String team, String opponent, double score, double opponentScore, double weightedScoreDiff, int week, LocalDate date, HashMap<String, Double> statistics) {
        this.team = team;
        this.opponent = opponent;
        this.score = score;
        this.opponentScore = opponentScore;
        this.weightedScoreDiff = weightedScoreDiff;
        this.week = week;
        this.date = date;
        this.statistics = new HashMap<>();
        for (String statistic : statistics.keySet()) {
            this.statistics.put(statistic, statistics.get(statistic));
        }
    }

    /**
     * Returns the name of the team of the game
     *
     * @return the name of the team of the game
     */
    public String getTeam() {
        return team;
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
     * Returns the signed, raw score of the game
     *
     * @return the signed, raw score of the game
     */
    public double getScoreDiff() {
        return score - opponentScore;
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
     * Returns the week during which the game occurred
     *
     * @return the week during which the game occurred
     */
    public int getWeek() {
        return week;
    }

    /**
     * Returns the time at which the game occurred
     *
     * @return the time at which the game occurred
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the statistic of a specified name
     *
     * @param statistic the name of the statistic
     * @return the value of the statistic
     */
    public double getStatistic(String statistic) {
        return statistics.get(statistic);
    }

    public Vector getStatisticsVector() {
        double[] vector = new double[2 + statistics.size()];
        vector[0] = score;
        vector[1] = opponentScore;
        int i = 0;
        for (Double statistic : statistics.values()) {
            vector[i++] = statistic;
        }
        return new Vector(vector);
    }

    public void addDefensiveStatistics(Game opponentGame) {
        for (String statistic : opponentGame.statistics.keySet()) {
            if (!statistic.startsWith("Opponent")) {
                this.statistics.put("Opponent" + statistic, opponentGame.statistics.get(statistic));
            }
        }
    }

    public boolean equalsReversed(Game game) {
        return this.team.equals(game.opponent) &&
                this.opponent.equals(game.team) &&
                this.date.equals(game.date);
    }

    /**
     * Returns a copy of the game
     *
     * @param game the game to be copied
     *
     * @return a copy of the game
     */
    static Game copyOf(Game game) {
        return new Game(game.team, game.opponent, game.score, game.opponentScore, game.weightedScoreDiff, game.week, game.date, game.statistics);
    }
}
