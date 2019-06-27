import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
import ratingsystems.common.ratingsystem.RatingSystem;
import rrs.RelativeRatingSystem;

public class Main {
    public static void main(String[] args) {
        try {
            RatingSystem rrs = new RelativeRatingSystem(new CollegeFootballInterpreter(), 2018);
            rrs.setup();
            rrs.printTeamRankings();

            System.out.println("==========");

            rrs = new RelativeRatingSystem(new CollegeFootballInterpreter(), 2018, 10);
            rrs.setup();
            rrs.printTeamRankings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
