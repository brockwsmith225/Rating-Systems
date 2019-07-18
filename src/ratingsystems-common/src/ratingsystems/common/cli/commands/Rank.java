package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.ratingsystem.RatingSystem;

public class Rank extends Command {
    @Override
    public void run(Runner runner, CommandInput commandInput) {
        RatingSystem ratingSystem = runner.loadRatingSystem(commandInput);

        System.out.println();
        ratingSystem.printTeamRankings(commandInput.getOption("pretty-print"));
        System.out.println();
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateDataExists(runner)) return false;
        return true;
    }
}
