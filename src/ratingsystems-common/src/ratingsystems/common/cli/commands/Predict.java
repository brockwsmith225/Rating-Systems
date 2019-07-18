package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.Terminal;

public class Predict extends Command {
    @Override
    public void run(Runner runner, CommandInput commandInput) {
        String team1 = commandInput.getArgs().get(0);
        String team2 = commandInput.getArgs().get(1);
        double odds = runner.loadRatingSystem(commandInput).predictGame(team1, team2);

        if (commandInput.getOption("pretty-print")) {
            System.out.println();
            System.out.println(team1 + " vs " + team2);
            System.out.println("----------");
            System.out.println(team1 + ": " + Terminal.round(odds * 100, 2) + "%");
            System.out.println(team2 + ": " + Terminal.round((1 - odds) * 100, 2) + "%");
            System.out.println();
        } else {
            System.out.println(odds);
        }
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateDataExists(runner)) return false;
        if (!Command.validateArgsExist(commandInput, 2)) return false;
        return true;
    }
}
