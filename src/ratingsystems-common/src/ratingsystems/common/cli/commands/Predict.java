package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;

public class Predict extends Command {
    @Override
    public void run(Runner runner, CommandInput commandInput) {
        String team1 = commandInput.getArgs().get(0);
        String team2 = commandInput.getArgs().get(1);
        System.out.println(runner.loadRatingSystem(commandInput));
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateDataExists(runner)) return false;
        if (!Command.validateArgsExist(commandInput, 2)) return false;
        return true;
    }
}
