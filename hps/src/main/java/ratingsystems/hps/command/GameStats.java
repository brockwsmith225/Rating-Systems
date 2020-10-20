package ratingsystems.hps.command;

import ratingsystems.common.Runner;
import ratingsystems.common.commands.Command;
import ratingsystems.common.commands.CommandMode;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.hps.HistoricalPredictionSystem;
import ratingsystems.hps.HistoricalPredictionSystemRunner;

import java.util.List;
import java.util.Map;

public class GameStats extends Command<HistoricalPredictionSystemRunner> {
    @Override
    public Object run(HistoricalPredictionSystemRunner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        HistoricalPredictionSystem ratingSystem = runner.loadRatingSystem(options, parameters);
        if (commandMode == CommandMode.TERMINAL) {
            System.out.println();
            System.out.println(ratingSystem.printGameStats());
            System.out.println();
        } else if (commandMode == CommandMode.API) {

        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        if (!Command.validateDataExists(runner, parameters)) return false;
        return true;
    }
}
