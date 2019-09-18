package ratingsystems.ser;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class SimpleEfficiencyRating extends RatingSystem {
    private double ppg;

    public SimpleEfficiencyRating() {
        super();
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int[] years) throws FileNotFoundException {
        super(interpreter, years);
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int[] years, int week) throws FileNotFoundException {
        super(interpreter, years, week);
    }

    @Override
    public void setup() {
        ppg = 0.0;

        calculateEfficiencies();

        rankTeams();
        rankGroups();
    }

    @Override
    public void rankGroups() {
        HashSet<String> addedGroups = new HashSet<>();
        HashMap<String, Team> groups = new HashMap<>();
        for (Team team : rankedTeams) {
            if (addedGroups.add(team.getGroup())) {
                groups.put(team.getGroup(), new Team(team.getGroup()));
            }
            Team group = groups.get(team.getGroup());
            group.setRating(group.getRating() * team.getRating());
        }
        rankedGroups = new ArrayList<>(groups.values());
        Collections.sort(rankedGroups);
    }

    @Override
    public Prediction predictGame(String team1, String team2) {
        if (!teams.keySet().contains(team1) || !teams.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        double team1Production = teams.get(team1).getRating("Offensive Rating") / teams.get(team2).getRating("Defensive Rating");
        double team2Production = teams.get(team2).getRating("Offensive Rating") / teams.get(team1).getRating("Defensive Rating");

        double team1Score = team1Production * ppg;
        double team2Score = team2Production * ppg;

        double odds = team1Production / (team1Production + team2Production);

        return new Prediction(team1, team2, odds, team1Score, team2Score);
    }

    @Override
    protected String prettyPrintRankingsHeader() {
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
    protected String prettyPrintColumnHeaders() {
        StringBuilder header = new StringBuilder();
        header.append("     " + Terminal.leftJustify("Team", 50) + "   " + Terminal.leftJustify("Rating", 10) + "   " + Terminal.leftJustify("Offense", 10) + "   " + Terminal.leftJustify("Defense", 10) + "   " + Terminal.leftJustify("Record", 10));
        return header.toString();
    }

    @Override
    protected String printTeam(String team) {
        return teams.get(team).getName() + "\t"
                + teams.get(team).getRating() + "\t"
                + teams.get(team).getRecord();
    }

    @Override
    protected String prettyPrintTeam(String team) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getRating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getRating("Offensive Rating"), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getRating("Defensive Rating"), 3), 10) + "   "
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
    }



    //========== SimpleEfficiencyRating only methods ==========

    /**
     * Calculates the season efficiencies of every team
     */
    private void calculateEfficiencies() {
        int count = 0;
        for (Team team : teams.values()) {
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

            team.setRating("Offensive Rating", offensiveEfficiency / games);
            if (Double.isNaN(team.getRating("Offensive Rating"))) {
                team.setRating("Offensive Rating", 1.0);
            }
            if (team.getRating("Offensive Rating") > 10.0) {
                team.setRating("Offensive Rating", 10.0);
            }
            if (team.getRating("Offensive Rating") < 0.1) {
                team.setRating("Offensive Rating", 0.1);
            }

            team.setRating("Defensive Rating", games / defensiveEfficiency);
            if (Double.isNaN(team.getRating("Defensive Rating"))) {
                team.setRating("Defensive Rating", 1.0);
            }
            if (team.getRating("Defensive Rating") > 10.0) {
                team.setRating("Defensive Rating", 10.0);
            }
            if (team.getRating("Defensive Rating") < 0.1) {
                team.setRating("Defensive Rating", 0.1);
            }

            team.setRating(team.getRating("Offensive Rating") * team.getRating("Defensive Rating"));
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
