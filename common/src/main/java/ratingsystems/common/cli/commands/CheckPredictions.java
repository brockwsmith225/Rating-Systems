package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.List;

public class CheckPredictions extends Command {
    @Override
    public String run(Runner runner, CommandInput commandInput) {
        StringBuilder result = new StringBuilder();
        RatingSystem yearRatingSystem = runner.loadRatingSystem(commandInput);
        int maxWeek = yearRatingSystem.getWeek();

        double gamesCorrect = 0.0;
        double totalError = 0.0;
        double totalAbsoluteError = 0.0;
        double numOfGames = 0.0;

        for (int week = 0; week < maxWeek; week++) {
            List<Game> games = yearRatingSystem.getGames(week + 1);
            RatingSystem ratingSystem = runner.loadRatingSystem(commandInput, week);
            double correct = ratingSystem.checkPreditions(games);
            double error = ratingSystem.checkError(games);
            double absoluteError = ratingSystem.checkAbsoluteError(games);
            gamesCorrect += correct;
            totalError += error;
            totalAbsoluteError += absoluteError;
            numOfGames += games.size();

            result.append("Week ");
            result.append(week + 1);
            result.append("\nPercent Correct: ");
            result.append(correct / games.size());
            result.append("\nError:           ");
            result.append(error / games.size());
            result.append("\nAbsolute Error:  ");
            result.append(absoluteError / games.size());
            result.append("\n");
        }
        result.append("Total Percent Correct: ");
        result.append(gamesCorrect / numOfGames);
        result.append("\nTotal Error:           ");
        result.append(totalError / numOfGames);
        result.append("Total Absolute Error:  ");
        result.append(totalAbsoluteError / numOfGames);

        return result.toString();
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateDataExists(runner)) return false;

        return true;
    }
}
