package simpleEfficiencyRating;

import interpreter.Interpreter;
import interpreter.datatypes.Game;
import interpreter.datatypes.Team;
import ratingSystem.RatingSystem;

import java.io.FileNotFoundException;

public class SimpleEfficiencyRating extends RatingSystem {

    public SimpleEfficiencyRating() {
        super();
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
    }

    public SimpleEfficiencyRating(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
    }

    @Override
    public void setup() {
        calculateEfficiencies();

        rankTeams();
        rankGroups();
    }

    @Override
    public void rankGroups() {}



    //========== SimpleEfficiencyRating only methods ==========

    /**
     * Calculates the season efficiencies of every team
     */
    private void calculateEfficiencies() {
        for (Team team : teams.values()) {
            int games = 0;
            double offensiveEfficiency = 0.0;
            double defensiveEfficiency = 0.0;
            for (Game game : team.getGames()) {
                games++;
                offensiveEfficiency += calculateOffensiveEfficiency(game);
                defensiveEfficiency += calculateDefensiveEfficiency(game);
            }
            team.setRating("Offensive Rating", offensiveEfficiency / games);
            team.setRating("Defensive Rating", games / defensiveEfficiency);
            team.setRating(offensiveEfficiency / defensiveEfficiency);
        }
    }

    /**
     * Calculates the offensive efficiency of a single team during a single game
     *
     * @param game the game for which the offensive efficiency will be calculated
     * @return the offensive efficiency of the team from the game
     */
    private double calculateOffensiveEfficiency(Game game) {
        return game.getScore() / teams.get(game.getOpponent()).getPointsAllowedPerGame();
    }

    /**
     * Calculates the defensive effeciency of a single team during a single game
     *
     * @param game the game for which the defensive efficiency will be calculated
     * @return the defensive efficiency of the team from the game
     */
    private double calculateDefensiveEfficiency(Game game) {
        return game.getOpponentScore() / teams.get(game.getOpponent()).getPointsPerGame();
    }

}
