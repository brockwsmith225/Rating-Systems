import cfb.CFBInterpreter;
import ratingSystem.RatingSystem;
import relativeRatingSystem.RelativeRatingSystem;

public class Main {
    public static void main(String[] args) {
        try {
            RatingSystem rrs = new RelativeRatingSystem(new CFBInterpreter(), 2018);
            rrs.setup();
            rrs.printTeamRankings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
