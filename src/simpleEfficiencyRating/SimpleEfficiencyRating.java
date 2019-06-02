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

    private double calculateOffensiveEfficiency(Game game) {
        return game.getScore() / teams.get(game.getOpponent()).getPointsAllowedPerGame();
    }

    private double calculateDefensiveEfficiency(Game game) {
        return game.getOpponentScore() / teams.get(game.getOpponent()).getPointsPerGame();
    }

}
