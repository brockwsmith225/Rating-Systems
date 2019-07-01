package ratingsystems.rrs;

import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

public class RelativeRatingSystemRunner extends Runner {

    public RelativeRatingSystemRunner() {
        super();
        prefix = " [RRS] ";
        try {
            ratingSystem = new RelativeRatingSystem(new CollegeFootballInterpreter(), 2018);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ratingSystem.setup();

        //Add RRS specific parameters here

        //Add RRS specific commands here
    }

    @Override
    public RatingSystem loadRatingSystem() {
        return this.ratingSystem;
    }

    public static void main(String[] args) {
        new Terminal(new RelativeRatingSystemRunner()).start();
    }
}
