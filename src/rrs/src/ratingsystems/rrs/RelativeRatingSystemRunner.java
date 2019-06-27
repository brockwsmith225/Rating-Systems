package ratingsystems.rrs;

import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;

public class RelativeRatingSystemRunner extends Runner {
    private static final String PREFIX = "[RRS]";

    public RelativeRatingSystemRunner() {
        super();
        //Add RRS specific commands here
    }

    public String getPrefix() {
        return PREFIX;
    }

    public static void main(String[] args) {
        new Terminal(new RelativeRatingSystemRunner()).start();
    }
}
