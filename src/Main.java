import collegeFootball.CollegeFootballInterpreter;
import ratingSystem.RatingSystem;
import relativeRatingSystem.RelativeRatingSystem;

public class Main {
    public static void main(String[] args) {
        try {
            RatingSystem rrs = new RelativeRatingSystem(new CollegeFootballInterpreter(), 2018);
            rrs.setup();
            rrs.printTeamRankings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
