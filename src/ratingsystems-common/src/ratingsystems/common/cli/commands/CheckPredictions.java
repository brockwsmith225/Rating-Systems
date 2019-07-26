package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.ratingsystem.RatingSystem;

public class CheckPredictions extends Command {
    @Override
    public void run(Runner runner, CommandInput commandInput) {
        RatingSystem yearRatingSystem = runner.loadRatingSystem(commandInput);

    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {


        return true;
    }
}
