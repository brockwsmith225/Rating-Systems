package ratingsystems.eps;

import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.parameters.Parameters;

import java.io.FileNotFoundException;
import java.util.Map;

public class EfficiencyPredictionSystemRunner extends Runner<EfficiencyPredictionSystem> {

    public EfficiencyPredictionSystemRunner() {
        super();
        prefix = " [EPS] ";

        //Add EPS specific parameters here

        //Add EPS specific commands here
    }

    @Override
    public EfficiencyPredictionSystem loadNewRatingSystem(Map<String, Boolean> options, Parameters parameters) {
        try {
            EfficiencyPredictionSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                    int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                    for (int i = 0; i < years.length; i++) {
                        years[i] = i + (int) parameters.getValue("START_YEAR");
                    }
                    ratingSystem = new EfficiencyPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            years,
                            (int) parameters.getValue("WEEK"),
                            options.get("CUMULATIVE"));
                } else {
                    ratingSystem = new EfficiencyPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            (int) parameters.getValue("YEAR"),
                            (int) parameters.getValue("WEEK"));
                }
            } else if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                for (int i = 0; i < years.length; i++) {
                    years[i] = i + (int) parameters.getValue("START_YEAR");
                }
                ratingSystem = new EfficiencyPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        years,
                        options.get("CUMULATIVE"));
            } else {
                ratingSystem = new EfficiencyPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        (int) parameters.getValue("YEAR"));
            }
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public EfficiencyPredictionSystem loadNewRatingSystem(Interpreter interpreter, int year) {
        try {
            EfficiencyPredictionSystem ratingSystem = new EfficiencyPredictionSystem(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public EfficiencyPredictionSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            EfficiencyPredictionSystem ratingSystem = new EfficiencyPredictionSystem(interpreter, year, week);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        new Terminal(new EfficiencyPredictionSystemRunner()).start();
    }

}
