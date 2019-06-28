package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.ratingsystem.RatingSystem;

public class Rank extends Command {
    @Override
    public void run(Runner runner, CommandInput commandInput) {
        RatingSystem ratingSystem = runner.loadRatingSystem();

        System.out.println();
        ratingSystem.printTeamRankings();
        System.out.println();
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        Command.validateDataExists(runner);
        return true;
    }
}