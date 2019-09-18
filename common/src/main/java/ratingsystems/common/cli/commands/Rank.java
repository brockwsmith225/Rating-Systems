package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.ratingsystem.RatingSystem;

public class Rank extends Command {
    @Override
    public Object run(Runner runner, CommandInput commandInput, CommandMode commandMode) {
        RatingSystem ratingSystem = runner.loadRatingSystem(commandInput);
        if (commandMode == CommandMode.TERMINAL) {
            return "\n" + ratingSystem.printTeamRankings(commandInput.getOption("pretty-print")) + "\n";
        } else if (commandMode == CommandMode.API) {
            return ratingSystem.getTeamRankings();
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateDataExists(runner)) return false;
        return true;
    }
}
