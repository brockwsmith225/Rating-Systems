package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.List;
import java.util.Map;

public class Playoff extends Command<Runner> {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        RatingSystem ratingSystem = runner.loadRatingSystem(options, parameters);
        if (commandMode == CommandMode.TERMINAL) {
            ratingSystem.predictPlayoff();
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        if (!Command.validateDataExists(runner, parameters)) return false;
        return true;
    }
}
