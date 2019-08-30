package ratingsystems.hps;

import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.common.linalg.Matrix;
import ratingsystems.common.linalg.Vector;

public class HistoricalPredictionSystem extends RatingSystem {

    private Matrix historicalMatrix;

    @Override
    public void setup() {

    }

    @Override
    public void rankGroups() {

    }

    @Override
    public Prediction predictGame(String team1, String team2) {
        if (!teams.keySet().contains(team1) || !teams.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        Vector team1Stats = teams.get(team1).getStatisticsVector();
        Vector team2Stats = teams.get(team2).getStatisticsVector();
        if (team1Stats == null || team2Stats == null) {
            return new Prediction(team1, team2, 0.5);
        }

        return null;
    }

}
