package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;

public class Predict extends Command {
    @Override
    public void run(Runner runner, CommandInput commandInput) {
        String team1 = commandInput.getArgs().get(0);
        String team2 = commandInput.getArgs().get(1);
        System.out.println(runner.loadRatingSystem().predictGame(team1, team2));
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        Command.validateDataExists(runner);
        Command.validateArgsExist(commandInput, 2);
        return true;
    }
}
