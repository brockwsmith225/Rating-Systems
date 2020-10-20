package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.List;
import java.util.Map;

public class Predictions extends Command<Runner> {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        RatingSystem ratingSystem = runner.loadRatingSystem(options, parameters);
        if (commandMode == CommandMode.TERMINAL) {
            System.out.println();
            if (parameters.containsKey("WEEK")) {
                Integer week = (Integer) parameters.getValue("WEEK");
                parameters.remove("WEEK");
                RatingSystem gamesRS = runner.loadRatingSystem(options, parameters);
                System.out.println(ratingSystem.printPredictions(gamesRS.getGames(week), options.get("PRETTY_PRINT")));
            } else {
                int maxWeek = ratingSystem.getWeek();
                for (int week = 0; week < maxWeek; week++) {
                    List<Game> games = ratingSystem.getGames(week + 1);
                    RatingSystem rs = runner.loadRatingSystem(options, parameters, week);
                    System.out.println(rs.printPredictions(games, options.get("PRETTY_PRINT")));
                }
            }
            System.out.println();
        } else if (commandMode == CommandMode.API) {
            return ratingSystem.getTeamRankings();
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        if (!Command.validateDataExists(runner, parameters)) return false;
        return true;
    }
}
