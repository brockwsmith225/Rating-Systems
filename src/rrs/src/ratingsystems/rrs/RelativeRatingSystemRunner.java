package ratingsystems.rrs;

import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;

public class RelativeRatingSystemRunner extends Runner {

    public RelativeRatingSystemRunner() {
        super();
        prefix = "[RRS]";
        ratingSystem = new RelativeRatingSystem();
        //Add RRS specific commands here
    }

    public static void main(String[] args) {
        new Terminal(new RelativeRatingSystemRunner()).start();
    }
}
