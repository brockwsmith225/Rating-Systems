package ratingsystems.rrs;

import ratingsystems.common.cli.parameters.Parameters;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;

public class RelativeRatingSystemRunner extends Runner {

    public RelativeRatingSystemRunner() {
        super();
        prefix = " [RRS] ";

        //Add RRS specific parameters here

        //Add RRS specific commands here
    }

    @Override
    public RatingSystem loadNewRatingSystem(Parameters parameters) {
        try {
            RatingSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                ratingSystem = new RelativeRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                                                    (int) parameters.getValue("YEAR"),
                                                    (int) parameters.getValue("WEEK"));
            } else {
                ratingSystem = new RelativeRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        (int) parameters.getValue("YEAR"));
            }
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public RatingSystem loadNewRatingSystem(Interpreter interpreter, int year) {
        try {
            RatingSystem ratingSystem = new RelativeRatingSystem(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public RatingSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            RatingSystem ratingSystem = new RelativeRatingSystem(interpreter, year, week);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new Terminal(new RelativeRatingSystemRunner()).start();
    }
}
