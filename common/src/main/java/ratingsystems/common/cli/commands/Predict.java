package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.parameters.Parameters;
import ratingsystems.common.ratingsystem.Prediction;

import java.util.List;
import java.util.Map;

public class Predict extends Command {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        String team1 = arguments.get(0);
        String team2 = arguments.get(1);
        Prediction prediction = runner.loadRatingSystem(options, parameters).predictGame(team1, team2);
        if (commandMode == CommandMode.TERMINAL) {
            if (options.get("PRETTY_PRINT")) {
                System.out.println();
                System.out.println(prediction.toString());
                System.out.println();
            } else {
                System.out.println(prediction.getOdds());
            }
        } else if (commandMode == CommandMode.API) {
            return prediction;
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        if (!Command.validateDataExists(runner, parameters)) return false;
        if (!Command.validateArgsExist(arguments, 2)) return false;
        return true;
    }
}
