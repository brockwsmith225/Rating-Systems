package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.cli.Terminal;
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
            double result = Math.random();
            System.out.println(result);
            if (result < prediction.getOdds()) {
                double gameControl = 1 - (result / (2 * prediction.getOdds()));
                double avgPoints = gameControl * prediction.getTeam1Score() + (1 - gameControl) * prediction.getTeam2Score();
                double line = 2 * avgPoints / 25 * (Math.log(1 - gameControl) - Math.log(gameControl));
                long team1Score = Math.round(avgPoints - line / 2);
                long team2Score = Math.round(avgPoints + line / 2);
                System.out.println(Terminal.leftJustify(team1, 20) + Terminal.rightJustify(Long.toString(team1Score), 5));
                System.out.println(Terminal.leftJustify(team2, 20) + Terminal.rightJustify(Long.toString(team2Score), 5));
            } else {
                double gameControl = (1 - result) / (2 * (1 - prediction.getOdds()));
                double avgPoints = gameControl * prediction.getTeam1Score() + (1 - gameControl) * prediction.getTeam2Score();
                double line = 2 * avgPoints / 25 * (Math.log(1 - gameControl) - Math.log(gameControl));
                long team1Score = Math.round(avgPoints - line / 2);
                long team2Score = Math.round(avgPoints + line / 2);
                System.out.println(Terminal.leftJustify(team1, 20) + Terminal.rightJustify(Long.toString(team1Score), 5));
                System.out.println(Terminal.leftJustify(team2, 20) + Terminal.rightJustify(Long.toString(team2Score), 5));
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
