import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
import ratingsystems.common.ratingsystem.RatingSystem;
import ser.SimpleEfficiencyRating;

public class Main {
    public static void main(String[] args) {
        try {
            RatingSystem ser = new SimpleEfficiencyRating(new CollegeFootballInterpreter(), 2018);
            ser.setup();
            ser.printTeamRankings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
