package ratingsystems.ser;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.*;

public class SimpleEfficiencyRating extends RatingSystem {
    private double ppg;
    private Map<String, SERTeam> teams;

    public SimpleEfficiencyRating() {
        super();
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SERTeam(super.teams.get(team)));
        }
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SERTeam(super.teams.get(team)));
        }
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int[] years, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SERTeam(super.teams.get(team)));
        }
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int[] years, int week, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, week, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SERTeam(super.teams.get(team)));
        }
    }

    @Override
    public void setup() {
        ppg = 0.0;

        calculateEfficiencies();

        rankTeams();
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        if (!teams.keySet().contains(team1) || !teams.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        double team1Production = teams.get(team1).getOffensiveRating() / teams.get(team2).getDefensiveRating();
        double team2Production = teams.get(team2).getOffensiveRating() / teams.get(team1).getDefensiveRating();

        double team1Score = team1Production * ppg;
        double team2Score = team2Production * ppg;

        double odds = team1Production / (team1Production + team2Production);

        return new Prediction(team1, team2, odds, team1Score, team2Score);
    }

    @Override
    protected String prettyPrintRankingsHeader(boolean allStats) {
        StringBuilder header = new StringBuilder();
        header.append("------------------------------------------------------------------------------------------------------------\n");
        if (week < 0) {
            header.append(Terminal.centerJustify(Integer.toString(year), 108));
        } else {
            header.append(Terminal.centerJustify(year + " Week " + week, 108));
        }
        header.append("\n------------------------------------------------------------------------------------------------------------\n");
        return header.toString();
    }

    @Override
    protected String prettyPrintColumnHeaders(boolean allStats) {
        return "     " + Terminal.leftJustify("Team", 50) + "   "
                + (!cumulative ? "Year   " : "")
                + Terminal.leftJustify("Rating", 10) + "   "
                + (allStats ? Terminal.leftJustify("Offense", 10) + "   "
                + Terminal.leftJustify("Defense", 10) + "   " : "")
                + Terminal.leftJustify("Record", 10);
    }

    @Override
    protected String printTeam(String team, boolean allStats) {
        return teams.get(team).getName() + "\t"
                + (!cumulative ? teams.get(team).getYear() + "\t" : "")
                + teams.get(team).getRating() + "\t"
                + (allStats ? teams.get(team).getOffensiveRating() + "\t"
                +  teams.get(team).getDefensiveRating() + "\t" : "")
                + teams.get(team).getRecord();
    }

    @Override
    protected String prettyPrintTeam(String team, boolean allStats) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + (!cumulative ? teams.get(team).getYear() + "   " : "")
                + Terminal.rightJustify(Terminal.round(teams.get(team).getRating(), 3), 10) + "   "
                + (allStats ? Terminal.rightJustify(Terminal.round(teams.get(team).getOffensiveRating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getDefensiveRating(), 3), 10) + "   " : "")
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
    }

    public SERTeam getSERTeam(String team) {
        if (teams.containsKey(team)) {
            return teams.get(team);
        }
        return null;
    }



    //========== SimpleEfficiencyRating only methods ==========

    /**
     * Calculates the season efficiencies of every team
     */
    private void calculateEfficiencies() {
        int count = 0;
        for (SERTeam team : teams.values()) {
            int games = 0;
            double offensiveEfficiency = 0.0;
            double defensiveEfficiency = 0.0;
            for (Game game : team.getGames()) {
                games++;
                offensiveEfficiency += calculateOffensiveEfficiency(game);
                defensiveEfficiency += calculateDefensiveEfficiency(game);
                ppg += game.getScore();
                count++;
            }

            team.setOffensiveRating(offensiveEfficiency / games);
            team.setDefensiveRating(games / defensiveEfficiency);
            team.calculateRating();
        }
        ppg /= count;
    }

    /**
     * Calculates the offensive efficiency of a single team during a single game
     *
     * @param game the game for which the offensive efficiency will be calculated
     * @return the offensive efficiency of the team from the game
     */
    private double calculateOffensiveEfficiency(Game game) {
        double offensiveEfficiency = game.getScore() / teams.get(game.getOpponent()).getPointsAllowedPerGame();
        if (Double.isNaN(offensiveEfficiency)) return 1.0;
        return offensiveEfficiency;
    }

    /**
     * Calculates the defensive effeciency of a single team during a single game
     *
     * @param game the game for which the defensive efficiency will be calculated
     * @return the defensive efficiency of the team from the game
     */
    private double calculateDefensiveEfficiency(Game game) {
        double defensiveEfficiency = game.getOpponentScore() / teams.get(game.getOpponent()).getPointsPerGame();
        if (Double.isNaN(defensiveEfficiency)) return 1.0;
        return defensiveEfficiency;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-1 * x));
    }

}
