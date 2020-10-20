package ratingsystems.ers;

import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.ers.EfficiencyRatingSystem;

import java.io.FileNotFoundException;
import java.util.Map;

public class EfficiencyRatingSystemRunner extends Runner<EfficiencyRatingSystem> {

    public EfficiencyRatingSystemRunner() {
        super();
        prefix = " [ERS] ";

        //Add RRS specific parameters here

        //Add RRS specific commands here
    }

    @Override
    public EfficiencyRatingSystem loadNewRatingSystem(Map<String, Boolean> options, Parameters parameters) {
        try {
            EfficiencyRatingSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                    int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                    for (int i = 0; i < years.length; i++) {
                        years[i] = i + (int) parameters.getValue("START_YEAR");
                    }
                    ratingSystem = new EfficiencyRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            years,
                            (int) parameters.getValue("WEEK"),
                            options.get("CUMULATIVE"));
                } else {
                    ratingSystem = new EfficiencyRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            (int) parameters.getValue("YEAR"),
                            (int) parameters.getValue("WEEK"));
                }
            } else if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                for (int i = 0; i < years.length; i++) {
                    years[i] = i + (int) parameters.getValue("START_YEAR");
                }
                ratingSystem = new EfficiencyRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        years,
                        options.get("CUMULATIVE"));
            } else {
                ratingSystem = new EfficiencyRatingSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        (int) parameters.getValue("YEAR"));
            }
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public EfficiencyRatingSystem loadNewRatingSystem(Interpreter interpreter, int year) {
        try {
            EfficiencyRatingSystem ratingSystem = new EfficiencyRatingSystem(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public EfficiencyRatingSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            EfficiencyRatingSystem ratingSystem = new EfficiencyRatingSystem(interpreter, year, week);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new Terminal(new EfficiencyRatingSystemRunner()).start();
    }

}
