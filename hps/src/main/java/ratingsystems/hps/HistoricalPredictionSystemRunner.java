package ratingsystems.hps;

import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;

public class HistoricalPredictionSystemRunner extends Runner {

    public HistoricalPredictionSystemRunner() {
        super();
        prefix = " [HPS] ";

        //Add HPS specific parameters here

        //Add HPS specific commands here
    }

    @Override
    public RatingSystem loadNewRatingSystem(Parameters parameters) {
        try {
            RatingSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                ratingSystem = new HistoricalPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        (int) parameters.getValue("YEAR"),
                        (int) parameters.getValue("WEEK"));
            } else {
                ratingSystem = new HistoricalPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
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
            RatingSystem ratingSystem = new HistoricalPredictionSystem(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public RatingSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            RatingSystem ratingSystem = new HistoricalPredictionSystem(interpreter, year, week);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new Terminal(new HistoricalPredictionSystemRunner()).start();
    }

}