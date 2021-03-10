package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.List;
import java.util.Map;

public class Stats  extends Command<Runner> {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        RatingSystem ratingSystem = runner.loadRatingSystem(options, parameters);
        String team = arguments.get(0);
        if (commandMode == CommandMode.TERMINAL) {
            System.out.println();
            if (options.get("PRETTY_PRINT")) {
                System.out.println(ratingSystem.prettyPrintTeamStats(team));
            } else {
                System.out.println(ratingSystem.printTeamStats(team));
            }
            System.out.println();
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        if (!Command.validateDataExists(runner, parameters)) return false;
        if (!Command.validateArgsExist(arguments, 1)) return false;
        return true;
    }
}
