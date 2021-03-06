package ratingsystems.rrs;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.linalg.Matrix;
import ratingsystems.common.linalg.Vector;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.*;

public class RelativeRatingSystem extends RatingSystem {

    protected Map<String, RRSTeam> teams;
    private Matrix posMatrix, negMatrix;
    protected Map<String, Integer> teamNameToIndex;
    protected Map<Integer, String> teamIndexToName;

    public RelativeRatingSystem() {
        super();
    }

    public RelativeRatingSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        this.teams = new HashMap<>();
        teamNameToIndex = new HashMap<>();
        teamIndexToName = new HashMap<>();
        int i = 0;
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new RRSTeam(super.teams.get(team)));
            teamNameToIndex.put(team, i);
            teamIndexToName.put(i, team);
            i++;
        }
    }

    public RelativeRatingSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        this.teams = new HashMap<>();
        teamNameToIndex = new HashMap<>();
        teamIndexToName = new HashMap<>();
        int i = 0;
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new RRSTeam(super.teams.get(team)));
            teamNameToIndex.put(team, i);
            teamIndexToName.put(i, team);
            i++;
        }
    }

    public RelativeRatingSystem(Interpreter interpreter, int[] years) throws FileNotFoundException {
        super(interpreter, years, true);
        this.teams = new HashMap<>();
        teamNameToIndex = new HashMap<>();
        teamIndexToName = new HashMap<>();
        int i = 0;
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new RRSTeam(super.teams.get(team)));
            teamNameToIndex.put(team, i);
            teamIndexToName.put(i, team);
            i++;
        }
    }

    public RelativeRatingSystem(Interpreter interpreter, int[] years, int week) throws FileNotFoundException {
        super(interpreter, years, week, true);
        this.teams = new HashMap<>();
        teamNameToIndex = new HashMap<>();
        teamIndexToName = new HashMap<>();
        int i = 0;
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new RRSTeam(super.teams.get(team)));
            teamNameToIndex.put(team, i);
            teamIndexToName.put(i, team);
            i++;
        }
    }

    @Override
    public void setup() {
        double[][] posValues = setupPositiveValues();
        double[][] negValues = setupNegativeValues();
        double[][] partials = setupPartials();

        this.posMatrix = convertToProbabiltyMatrix(posValues);
        this.negMatrix = convertToProbabiltyMatrix(negValues);
        Matrix partialsMatrix = new Matrix(partials);

        Matrix posMatrix = this.posMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));
        Matrix negMatrix = this.negMatrix.multiply(0.9).add(partialsMatrix.multiply(0.1));

        ratingsystems.common.linalg.Vector posVector = posMatrix.getEigenvector(1.0);
        ratingsystems.common.linalg.Vector negVector = negMatrix.getEigenvector(1.0);

        setPositiveRatings(posVector);
        setNegativeRatings(negVector);
        setRatings();

        rankTeams();
    }

    @Override
    public void rankTeams() {
        rankedTeams = new ArrayList<>(teams.values());
        Collections.sort(rankedTeams);
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        if (!teamNameToIndex.keySet().contains(team1) || !teamNameToIndex.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(teamNameToIndex.get(team1));
        indices.add(teamNameToIndex.get(team2));

        Matrix posMatrix = removeExtraneousConnections(this.posMatrix, indices);//this.posMatrix.copy();
        Matrix negMatrix = removeExtraneousConnections(this.negMatrix, indices);//this.negMatrix.copy();

        double[] input = new double[teams.size()];
        for (int i = 0; i < input.length; i++) {
            input[i] = 0;
        }
        for (int i : indices) {
            input[i] = 0.5;
        }
        ratingsystems.common.linalg.Vector inputVector = new ratingsystems.common.linalg.Vector(input);

        ratingsystems.common.linalg.Vector posVector = inputVector.copy();
        ratingsystems.common.linalg.Vector negVector = inputVector.copy();
        for (int i = 0; i < 1; i++) {
            posVector = posMatrix.multiply(posVector);
            negVector = negMatrix.multiply(negVector);
        }

        double team1PosScore = posVector.get(indices.get(0));
        double team1NegScore = negVector.get(indices.get(0));
        double team2PosScore = posVector.get(indices.get(1));
        double team2NegScore = negVector.get(indices.get(1));

        double posDiff = team1PosScore - team2PosScore;
        double negDiff = team2NegScore - team1NegScore;
        double totalDiff = posDiff + negDiff;

        double a = 0.9;
        double b = 1.8;
        double odds = a * Math.tan(Math.atan(0.5 / a) * Math.tanh(b * totalDiff)) + 0.5;

        return new Prediction(team1, team2, odds);
    }

    @Override
    protected String printTeam(String team, boolean allStats) {
        return teams.get(team).getName() + "\t"
                + teams.get(team).getRating() + "\t"
                + teams.get(team).getRecord();
    }

    @Override
    protected String prettyPrintTeam(String team, boolean allStats) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + Terminal.rightJustify(Integer.toString((int)teams.get(team).getRating()), 10) + "   "
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
    }

    @Override
    public String printTeamStats(String t) {
        RRSTeam team = teams.get(t);
        String stats = team.getName() + "\t"
                + team.getRecord() + "\t"
                + team.getRating() + "\t"
                + team.getPositiveRating() + "\t"
                + team.getNegativeRating() + "\n";
        Map<String, Double> opponentTotalsWins = new HashMap<>();
        Map<String, Double> opponentTotalsLosses = new HashMap<>();
        Map<String, Double> opponentPosCol = new HashMap<>();
        Map<String, Double> opponentNegCol = new HashMap<>();
        double posTotal = posMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(t));
        double negTotal = negMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(t));
        double totalScore = 0.0;
        for (Game game : team.getGames()) {
            String opponent = game.getOpponent();
            totalScore += Math.abs(game.getWeightedScoreDiff());
            if (game.getScoreDiff() > 0) {
                if (!opponentTotalsWins.containsKey(opponent)) {
                    opponentTotalsWins.put(opponent, 0.0);
//                    negTotal += posMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
                }
                opponentTotalsWins.put(opponent, opponentTotalsWins.get(opponent) + game.getWeightedScoreDiff());
                double colTotal = 0.0;
                for (int i = 0; i < posMatrix.rows(); i++) {
                    colTotal += posMatrix.get(i, teamNameToIndex.get(opponent));
                }
                opponentPosCol.put(opponent, colTotal);
            } else {
                if (!opponentTotalsLosses.containsKey(opponent)) {
                    opponentTotalsLosses.put(opponent, 0.0);
//                    posTotal += negMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
                }
                opponentTotalsLosses.put(opponent, opponentTotalsLosses.get(opponent) + game.getWeightedScoreDiff());
                double colTotal = 0.0;
                for (int i = 0; i < negMatrix.rows(); i++) {
                    colTotal += negMatrix.get(i, teamNameToIndex.get(opponent));
                }
                opponentNegCol.put(opponent, colTotal);
            }
        }
        posTotal = 0.0;
        negTotal = 0.0;
        for (int i = 0; i < posMatrix.rows(); i++) {
            posTotal += posMatrix.get(i, teamNameToIndex.get(t));
            negTotal += negMatrix.get(i, teamNameToIndex.get(t));
        }
        double total = 0.0;
        for (Game game : team.getGames()) {
            String opponent = game.getOpponent();
            double change, pctChange = 0.0;
            if (game.getScoreDiff() > 0) {
                double gamePortion = game.getWeightedScoreDiff() / opponentTotalsWins.get(opponent);
                double gameScore = gamePortion * posMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
                change = gameScore / opponentPosCol.get(opponent) * teams.get(opponent).getPositiveRating()
                        + 0.9 * game.getWeightedScoreDiff() / totalScore * team.getNegativeRating()
                        + 0.9 * game.getWeightedScoreDiff() / totalScore * team.getPositiveRating()
                ;
            } else {
                double gamePortion = game.getWeightedScoreDiff() / opponentTotalsLosses.get(opponent);
                double gameScore = gamePortion * negMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
                change = gameScore / opponentNegCol.get(opponent) * teams.get(opponent).getNegativeRating()
                        + 0.9 * game.getWeightedScoreDiff() / totalScore * team.getPositiveRating()
                        + 0.9 * game.getWeightedScoreDiff() / totalScore * team.getNegativeRating()
                ;
            }
            stats += opponent + "\t"
                    + (game.getScoreDiff() > 0 ? "W" : "L") + "\t"
                    + game.getScore() + "\t"
                    + game.getOpponentScore() + "\t"
                    + (game.getScoreDiff() > 0 ? "" : "-") + change + "\t"
                    + (game.getScoreDiff() > 0 ? "" : "-") + pctChange + "\n";
        }
        System.out.println(totalScore);
        System.out.println(total);
        return stats;
    }

    @Override
    public String prettyPrintTeamStats(String t) {
        RRSTeam team = teams.get(t);
        String stats = team.getName() + " ("
                + team.getRecord() + ") "
                + Terminal.round(team.getRating(),0) + "\n"
                + "Wins:   " + Terminal.round(team.getPositiveRating(), 0) + "\n"
                + "Losses: " + Terminal.round(team.getNegativeRating(), 0) + "\n";
        Map<String, Double> opponentTotalsWins = new HashMap<>();
        Map<String, Double> opponentTotalsLosses = new HashMap<>();
        double posTotal = 0.0;
        double negTotal = 0.0;
        for (Game game : team.getGames()) {
            String opponent = game.getOpponent();
            if (game.getScoreDiff() > 0) {
                if (!opponentTotalsWins.containsKey(opponent)) {
                    opponentTotalsWins.put(opponent, 0.0);
                    posTotal += posMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
                }
                opponentTotalsWins.put(opponent, opponentTotalsWins.get(opponent) + game.getWeightedScoreDiff());
            } else {
                if (!opponentTotalsLosses.containsKey(opponent)) {
                    opponentTotalsLosses.put(opponent, 0.0);
                    negTotal += negMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
                }
                opponentTotalsLosses.put(opponent, opponentTotalsLosses.get(opponent) + game.getWeightedScoreDiff());
            }
        }
        for (Game game : team.getGames()) {
            String opponent = game.getOpponent();
            double change, pctChange = 0.0;
            if (game.getScoreDiff() > 0) {
                double gamePortion = game.getWeightedScoreDiff() / opponentTotalsWins.get(opponent);
                change = gamePortion * posMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
            } else {
                double gamePortion = game.getWeightedScoreDiff() / opponentTotalsLosses.get(opponent);
                change = gamePortion * negMatrix.get(teamNameToIndex.get(t), teamNameToIndex.get(opponent));
            }
            stats += Terminal.leftJustify(opponent, 25) + "   "
                    + (game.getScoreDiff() > 0 ? "W" : "L") + "   "
                    + Terminal.rightJustify(Terminal.round(game.getScore(), 0), 3) + " - "
                    + Terminal.leftJustify(Terminal.round(game.getOpponentScore(), 0), 3) + "   "
                    + Terminal.rightJustify((game.getScoreDiff() > 0 ? "+" : "-") + Terminal.round(change, 2), 6) + "   "
                    + Terminal.rightJustify((game.getScoreDiff() > 0 ? "+" : "-") + Terminal.round(pctChange * 100, 2), 6) + "%\n";
        }
        return stats;
    }

    public boolean hasTeam(String team) {
        return teams.containsKey(team);
    }



    //========== RelativeRatingSystem only methods ==========

    /**
     * Sets up a 2D array with the values from all games which are positive for the team associated with it
     *
     * @return the 2D array with all positive values
     */
    protected double[][] setupPositiveValues() {
        double[][] values = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String team : teams.keySet()) {
            List<Game> games = teams.get(team).getGames();
            double totalWeightedScoreDiff = 0.0;
            for (Game game : games) {
                if (game.getScoreDiff() > 0) {
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] += Math.abs(game.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(game.getWeightedScoreDiff());
                }
            }
            values[teamNameToIndex.get(team)][teamNameToIndex.get(team)] = totalWeightedScoreDiff;
        }
        return values;
    }

    /**
     * Sets up a 2D array with the values from all games which are negative for the team associated with it
     *
     * @return the 2D array with all negative values
     */
    protected double[][] setupNegativeValues() {
        double[][] values = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                values[r][c] = 0.0;
            }
        }
        for (String team : teams.keySet()) {
            List<Game> games = teams.get(team).getGames();
            double totalWeightedScoreDiff = 0.0;
            for (Game game : games) {
                if (game.getScoreDiff() < 0) {
                    values[teamNameToIndex.get(team)][teamNameToIndex.get(game.getOpponent())] += Math.abs(game.getWeightedScoreDiff());
                    totalWeightedScoreDiff += Math.abs(game.getWeightedScoreDiff());
                }
            }
            values[teamNameToIndex.get(team)][teamNameToIndex.get(team)] = totalWeightedScoreDiff;
        }
        return values;
    }

    /**
     * Sets up a 2D array with the partial values necessary for guaranteeing that the matrices will be regular
     * stochastic matrices (which is necessary for the row reduction step)
     *
     * @return the 2D array with the partial values
     */
    private double[][] setupPartials() {
        double[][] partials = new double[teams.size()][teams.size()];
        for (int r = 0; r < teams.size(); r++) {
            for (int c = 0; c < teams.size(); c++) {
                partials[r][c] = 1.0 / teams.size();
            }
        }
        return partials;
    }

    /**
     * Convertes a 2D array of values to a probability matrix
     *
     * @param values the 2D array of values to be converted
     * @return a probability matrix made from the inputted values
     */
    private Matrix convertToProbabiltyMatrix(double[][] values) {
        for (int c = 0; c < values[0].length; c++) {
            double colSum = 0.0;
            for (int r = 0; r < values.length; r++) {
                colSum += values[r][c];
            }
            if (colSum > 0.0) {
                for (int r = 0; r < values.length; r++) {
                    values[r][c] /= colSum;
                }
            } else {
                for (int r = 0; r < values.length; r++) {
                    values[r][c] = 1.0 / values[r].length;
                }
            }
        }
        return new Matrix(values);
    }

    /**
     * Sets the rating that comes from the positive value matrix for every team
     *
     * @param vector the eigenvector corresponding to the positive ratings
     */
    private void setPositiveRatings(ratingsystems.common.linalg.Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            teams.get(teamIndexToName.get(i)).setPositiveRating((vector.get(i) * 10000));
        }
    }

    /**
     * Sets the rating that comes from the negative value matrix for every team
     *
     * @param vector the eigenvector corresponding to the negative ratings
     */
    private void setNegativeRatings(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            teams.get(teamIndexToName.get(i)).setNegativeRating((vector.get(i) * 10000));
        }
    }

    /**
     * Sets the ratings for every team based on its positive and negative ratings
     */
    private void setRatings() {
        for (String team : teams.keySet()) {
            teams.get(team).calculateRating();
        }
    }

    private Matrix removeExtraneousConnections(Matrix matrix, List<Integer> indices) {
        Matrix matrixCopy = matrix.copy();
        for (int c = 0; c < matrixCopy.columns(); c++) {
            if (!indices.contains(c)) {
                double value = matrixCopy.get(c, c);
                double count = 0.0;
                matrixCopy.set(c, c, 0.0);
                for (int r = 0; r < matrixCopy.rows(); r++) {
                    if (matrixCopy.get(r, c) > 0) {
                        count++;
                    }
                }
                value /= count;
                for (int r = 0; r < matrixCopy.rows(); r++) {
                    if (matrixCopy.get(r, c) > 0) {
                        matrixCopy.set(r, c, matrixCopy.get(r, c) + value);
                    }
                }
            }
        }
        return matrixCopy;
    }

    @Override
    public void predictPlayoff() {
        HashMap<String, Double> odds = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            double cutoff = rankedTeams.get(i).getRating() * 0.75;
            if (rankedTeams.get(4).getRating() < cutoff) {
                odds.put(rankedTeams.get(i).getName(), 1.0);
            } else {
                int j = i;
                double ratingSum = 0.0;
                for (; j < rankedTeams.size() && rankedTeams.get(j).getRating() >= cutoff; j++) {
                    ratingSum += rankedTeams.get(j).getRating();
                }
                double[] ratings = new double[j - i];
                double modifiedSum = 0.0;
                for (int k = i; k < j; k++) {
                    ratings[k-i] = rankedTeams.get(k).getRating() / ratingSum;
                    modifiedSum += Math.exp(ratings[k-i]) - 1;
                }
                for (int k = i; k < j; k++) {
                    ratings[k-i] = (Math.exp(ratings[k-i]) - 1) / modifiedSum;
                    if (odds.containsKey(rankedTeams.get(k).getName())) {
                        odds.put(rankedTeams.get(k).getName(), odds.get(rankedTeams.get(k).getName() + ratings[k-i]));
                    } else {
                        odds.put(rankedTeams.get(k).getName(), ratings[k-i]);
                    }
                }
            }
        }
        for (String team : odds.keySet()) {
            System.out.println(team + "\t" + odds.get(team));
        }
    }
}
