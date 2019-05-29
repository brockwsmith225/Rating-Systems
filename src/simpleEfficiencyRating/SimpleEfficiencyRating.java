package simpleEfficiencyRating;

import ratingSystem.RatingSystem;

import java.util.ArrayList;
import java.util.Collections;

public class SimpleEfficiencyRating extends RatingSystem {

    @Override
    public void setup() {


        rankTeams();
        rankGroups();
    }

    @Override
    public void rankTeams() {
        rankedTeams = new ArrayList<>(teams.values());
        Collections.sort(rankedTeams);
    }

    @Override
    public void rankGroups() {}



    //========== SimpleEfficiencyRating only methods ==========

}
