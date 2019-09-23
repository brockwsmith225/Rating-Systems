package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.parameters.ParameterMap;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.List;
import java.util.Map;

public class Rank extends Command {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, ParameterMap parameters, CommandMode commandMode) {
        RatingSystem ratingSystem = runner.loadRatingSystem(options, parameters);
        if (commandMode == CommandMode.TERMINAL) {
            System.out.println();
            System.out.println(ratingSystem.printTeamRankings(options.get("pretty-print")));
            System.out.println();
        } else if (commandMode == CommandMode.API) {
            return ratingSystem.getTeamRankings();
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, ParameterMap parameters) {
        if (!Command.validateDataExists(runner, parameters)) return false;
        return true;
    }
}
