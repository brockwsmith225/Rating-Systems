package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;
import ratingsystems.common.ratingsystem.Prediction;

public class Predict extends Command {
    @Override
    public Object run(Runner runner, CommandInput commandInput, CommandMode commandMode) {
        String team1 = commandInput.getArgs().get(0);
        String team2 = commandInput.getArgs().get(1);
        Prediction prediction = runner.loadRatingSystem(commandInput).predictGame(team1, team2);

        if (commandInput.getOption("pretty-print")) {
            return prediction.toString();
        } else {
            return Double.toString(prediction.getOdds());
        }
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateDataExists(runner)) return false;
        if (!Command.validateArgsExist(commandInput, 2)) return false;
        return true;
    }
}
