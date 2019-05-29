package simpleEfficiencyRating;

import interpreter.datatypes.Game;
import interpreter.datatypes.Team;
import ratingSystem.RatingSystem;

public class SimpleEfficiencyRating extends RatingSystem {

    @Override
    public void setup() {
        calculateEfficiencies();

        rankTeams();
        rankGroups();
    }

    @Override
    public void rankGroups() {}



    //========== SimpleEfficiencyRating only methods ==========

    public void calculateEfficiencies() {
        for (Team team : teams.values()) {
            int games = 0;
            double offensiveEfficiency = 0.0;
            double defensiveEfficiency = 0.0;
            for (Game game : team.getGames()) {
                games++;
            }
        }
    }

    public double calculateOffensiveEfficiency(Game game) {

    }

    public double calculateDefensiveEfficiency(Game game) {

    }

}
