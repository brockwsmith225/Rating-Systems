package ratingsystems.rrs;

import ratingsystems.common.commands.Playoff;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.Map;

public class RelativeRatingSystemRunner extends Runner<RelativeRatingSystem> {

    public RelativeRatingSystemRunner() {
        super();
        prefix = " [RRS] ";

        //Add RRS specific parameters here

        //Add RRS specific commands here
        commands.put("playoff", new Playoff());
    }

    @Override
    public RelativeRatingSystem loadNewRatingSystem(Map<String, Boolean> options, Parameters parameters) {
        try {
            RelativeRatingSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                    int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                    for (int i = 0; i < years.length; i++) {
                        years[i] = i + (int) parameters.getValue("START_YEAR");
                    }
                    ratingSystem = new RelativeRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            years,
                            (int) parameters.getValue("WEEK"));
                } else {
                    ratingSystem = new RelativeRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            (int) parameters.getValue("YEAR"),
                            (int) parameters.getValue("WEEK"));
                }
            } else if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                for (int i = 0; i < years.length; i++) {
                    years[i] = i + (int) parameters.getValue("START_YEAR");
                }
                ratingSystem = new RelativeRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        years);
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
    public RelativeRatingSystem loadNewRatingSystem(Interpreter interpreter, int year) {
        try {
            RelativeRatingSystem ratingSystem = new RelativeRatingSystem(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public RelativeRatingSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            RelativeRatingSystem ratingSystem = new RelativeRatingSystem(interpreter, year, week);
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
