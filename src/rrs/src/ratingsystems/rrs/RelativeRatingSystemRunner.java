package ratingsystems.rrs;

import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.collegefootball.CollegeFootballInterpreter;

public class RelativeRatingSystemRunner extends Runner {

    public RelativeRatingSystemRunner() {
        super();
        prefix = "[RRS]";
        try {
            ratingSystem = new RelativeRatingSystem(new CollegeFootballInterpreter(), 2018);
        } catch (Exception e) {}
        ratingSystem.setup();
        //Add RRS specific commands here
    }

    public static void main(String[] args) {
        new Terminal(new RelativeRatingSystemRunner()).start();
    }
}
