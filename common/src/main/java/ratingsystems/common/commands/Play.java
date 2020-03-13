package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.ratingsystem.Prediction;

import java.util.List;
import java.util.Map;

public class Play extends Command {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        String team1 = arguments.get(0);
        String team2 = arguments.get(1);
        Location location = parameters.containsKey("LOCATION") ? (Location) parameters.getValue("LOCATION") : Location.NEUTRAL;
        Prediction prediction = runner.loadRatingSystem(options, parameters).predictGame(team1, team2, location);
        if (commandMode == CommandMode.TERMINAL) {
            if (Math.random() < prediction.getOdds()) {
                System.out.println(team1 + " wins");
            } else {
                System.out.println(team2 + " wins");
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
