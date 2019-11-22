package ratingsystems.rhps;

import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.Map;

public class RelativeHistoricalPredictionSystemRunner  extends Runner {

    public RelativeHistoricalPredictionSystemRunner() {
        super();
        prefix = " [RHPS] ";

        //Add RHPS specific parameters here

        //Add RHPS specific commands here
    }

    @Override
    public RatingSystem loadNewRatingSystem(Map<String, Boolean> options, Parameters parameters) {
        try {
            RatingSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                ratingSystem = new RelativeHistoricalPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        (int) parameters.getValue("YEAR"),
                        (int) parameters.getValue("WEEK"));
            } else {
                ratingSystem = new RelativeHistoricalPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
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
            RatingSystem ratingSystem = new RelativeHistoricalPredictionSystem(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public RatingSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            RatingSystem ratingSystem = new RelativeHistoricalPredictionSystem(interpreter, year, week);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new Terminal(new RelativeHistoricalPredictionSystemRunner()).start();
    }
}
