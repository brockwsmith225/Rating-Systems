package ratingsystems.ser;

import ratingsystems.common.cli.parameters.ParameterMap;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;

public class SimpleEfficiencyRatingRunner extends Runner {

    public SimpleEfficiencyRatingRunner() {
        super();
        prefix = " [SER] ";

        //Add RRS specific parameters here

        //Add RRS specific commands here
    }

    @Override
    public RatingSystem loadNewRatingSystem(ParameterMap parameters) {
        try {
            RatingSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                ratingSystem = new SimpleEfficiencyRating(interpreters.get((String) parameters.getValue("LEAGUE")),
                        (int) parameters.getValue("YEAR"),
                        (int) parameters.getValue("WEEK"));
            } else {
                ratingSystem = new SimpleEfficiencyRating(interpreters.get((String) parameters.getValue("LEAGUE")),
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
            RatingSystem ratingSystem = new SimpleEfficiencyRating(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public RatingSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            RatingSystem ratingSystem = new SimpleEfficiencyRating(interpreter, year, week);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new Terminal(new SimpleEfficiencyRatingRunner()).start();
    }

}
