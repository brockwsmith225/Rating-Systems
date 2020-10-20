package ratingsystems.hps;

import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.hps.command.GameStats;

import java.io.FileNotFoundException;
import java.util.Map;

public class HistoricalPredictionSystemRunner extends Runner<HistoricalPredictionSystem> {

    public HistoricalPredictionSystemRunner() {
        super();
        prefix = " [HPS] ";

        //Add HPS specific parameters here

        //Add HPS specific commands here
        commands.put("game-stats", new GameStats());
    }

    @Override
    public HistoricalPredictionSystem loadNewRatingSystem(Map<String, Boolean> options, Parameters parameters) {
        try {
            HistoricalPredictionSystem ratingSystem;
            if (parameters.containsKey("WEEK")) {
                if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                    int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                    for (int i = 0; i < years.length; i++) {
                        years[i] = i + (int) parameters.getValue("START_YEAR");
                    }
                    ratingSystem = new HistoricalPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            years,
                            (int) parameters.getValue("WEEK"));
                } else {
                    ratingSystem = new HistoricalPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                            (int) parameters.getValue("YEAR"),
                            (int) parameters.getValue("WEEK"));
                }
            } else if (parameters.containsKey("START_YEAR") && (int) parameters.getValue("START_YEAR") < (int) parameters.getValue("YEAR")) {
                int[] years = new int[(int) parameters.getValue("YEAR") - (int) parameters.getValue("START_YEAR") + 1];
                for (int i = 0; i < years.length; i++) {
                    years[i] = i + (int) parameters.getValue("START_YEAR");
                }
                ratingSystem = new HistoricalPredictionSystem(interpreters.get((String) parameters.getValue("LEAGUE")),
                        years);
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
    public HistoricalPredictionSystem loadNewRatingSystem(Interpreter interpreter, int year) {
        try {
            HistoricalPredictionSystem ratingSystem = new HistoricalPredictionSystem(interpreter, year);
            ratingSystem.setup();
            return ratingSystem;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public HistoricalPredictionSystem loadNewRatingSystem(Interpreter interpreter, int year, int week) {
        try {
            HistoricalPredictionSystem ratingSystem = new HistoricalPredictionSystem(interpreter, year, week);
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
