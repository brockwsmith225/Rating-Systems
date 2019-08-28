package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.List;

public class CheckPredictions extends Command {
    @Override
    public void run(Runner runner, CommandInput commandInput) {
        RatingSystem yearRatingSystem = runner.loadRatingSystem(commandInput);
        int maxWeek = yearRatingSystem.getWeek();

        double gamesCorrect = 0.0;
        double numOfGames = 0.0;

        for (int week = 15; week < maxWeek; week++) {
            List<Game> games = yearRatingSystem.getGames(week + 1);
            RatingSystem ratingSystem = runner.loadRatingSystem(commandInput, week);
            double correct = ratingSystem.checkPreditions(games);
            gamesCorrect += correct;
            numOfGames += games.size();
            System.out.println("Week " + (week + 1) + ": " + correct / games.size());
        }
        System.out.println("Total: " + gamesCorrect / numOfGames);
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateDataExists(runner)) return false;

        return true;
    }
}
