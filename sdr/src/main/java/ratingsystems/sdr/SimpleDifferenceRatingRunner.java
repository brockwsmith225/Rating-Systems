package ratingsystems.sdr;

import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.Map;

public class SimpleDifferenceRatingRunner extends Runner<SimpleDifferenceRating> {

    public SimpleDifferenceRatingRunner() {
        super();
        prefix = " [SDR] ";

        //Add RRS specific parameters here

        //Add RRS specific commands here
    }

    @Override
    public SimpleDifferenceRating loadNewRatingSystem(Map<String, Boolean> options, Parameters parameters) {
        try {
            SimpleDifferenceRating ratingSystem;
            if (parameters.containsKey("WEEK")) {
                if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                    int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                    for (int i = 0; i < years.length; i++) {
                        years[i] = i + (int) parameters.getValue("START_YEAR");
                    }
                    ratingSystem = new SimpleDifferenceRating(interpreters.get((String) parameters.getValue("LEAGUE")),
                            years,
                            (int) parameters.getValue("WEEK"),
                            options.get("CUMULATIVE"));
                } else {
                    ratingSystem = new SimpleDifferenceRating(interpreters.get((String) parameters.getValue("LEAGUE")),
                            (int) parameters.getValue("YEAR"),
                            (int) parameters.getValue("WEEK"));
                }
            } else if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                for (int i = 0; i < years.length; i++) {
                    years[i] = i + (int) parameters.getValue("START_YEAR");
                }
                ratingSystem = new SimpleDifferenceRating(interpreters.get((String) parameters.getValue("LEAGUE")),
                        years,
                        options.get("CUMULATIVE"));
            } else {
                ratingSystem = new SimpleDifferenceRating(interpreters.get((String) parameters.getValue("LEAGUE")),
                        (int) parameters.getValue("YEAR"));
            }
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public SimpleDifferenceRating loadNewRatingSystem(Interpreter interpreter, int year) {
        try {
            SimpleDifferenceRating ratingSystem = new SimpleDifferenceRating(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public SimpleDifferenceRating loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            SimpleDifferenceRating ratingSystem = new SimpleDifferenceRating(interpreter, year, week);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new Terminal(new SimpleDifferenceRatingRunner()).start();
    }

}
