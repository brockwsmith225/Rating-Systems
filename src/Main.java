import collegeFootball.CollegeFootballInterpreter;
import ratingSystem.RatingSystem;
import relativeRatingSystem.RelativeRatingSystem;
import simpleEfficiencyRating.SimpleEfficiencyRating;

public class Main {
    public static void main(String[] args) {
        try {
            RatingSystem rrs = new SimpleEfficiencyRating(new CollegeFootballInterpreter(), 2018);
            rrs.setup();
            rrs.printTeamRankings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
