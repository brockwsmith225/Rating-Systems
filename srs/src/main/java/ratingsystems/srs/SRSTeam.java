package ratingsystems.srs;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.RatingSystem;

public class SRSTeam extends Team {
    private double offensiveRating, defensiveRating;
    private double quad1Rating, quad2Rating, quad3Rating, quad4Rating;
    private double homeRating, awayRating;

    public SRSTeam(Team team) {
        super(team);
        this.offensiveRating = 1.0;
        this.defensiveRating = 1.0;
    }

    public void calculateRating() {
        this.setRating(this.offensiveRating * this.defensiveRating);
        //team.setRating(Math.sqrt(this.offensiveRating * this.defensiveRating));
    }

    public void setOffensiveRating(double offensiveRating) {
        if (Double.isNaN(offensiveRating)) {
            this.offensiveRating = 1.0;
        } else {
            this.offensiveRating = offensiveRating;
        }
    }

    public void setDefensiveRating(double defensiveRating) {
        if (Double.isNaN(defensiveRating)) {
            this.defensiveRating = 1.0;
        } else {
            this.defensiveRating = defensiveRating;
        }
    }

    public double getPointsPerGameStdDev() {
        double std = 0.0;
        double mean = this.getPointsPerGame();
        for (Game game : this.getGames()) {
            std += Math.pow(game.getScore() - mean, 2);
        }
        return Math.sqrt(std / numberOfGames);
    }

    public double getPointsAllowedPerGameStdDev() {
        double std = 0.0;
        double mean = this.getPointsAllowedPerGame();
        for (Game game : this.getGames()) {
            std += Math.pow(game.getOpponentScore() - mean, 2);
        }
        return Math.sqrt(std / numberOfGames);
    }

    public double getAdjustedPointsPerGame(RatingSystem ratingSystem) {
        double adjPPG = 0.0;
        for (Game game : this.getGames()) {
            adjPPG += game.getScore() - ratingSystem.getTeam(game.getOpponent()).getPointsAllowedPerGame();
        }
        return adjPPG / numberOfGames;
    }

    public double getAdjustedPointsAllowedPerGame(RatingSystem ratingSystem) {
        double adjPPG = 0.0;
        for (Game game : this.getGames()) {
            adjPPG += game.getOpponentScore() - ratingSystem.getTeam(game.getOpponent()).getPointsPerGame();
        }
        return adjPPG / numberOfGames;
    }

    public double getAdjustedPointsPerGameStdDev(RatingSystem ratingSystem) {
        double adjStd = 0.0;
        double adjMean = getAdjustedPointsPerGame(ratingSystem);
        for (Game game : this.getGames()) {
            adjStd += Math.pow((game.getScore() - ratingSystem.getTeam(game.getOpponent()).getPointsAllowedPerGame()) - adjMean, 2);
        }
        return Math.sqrt(adjStd / numberOfGames);
    }

    public double getAdjustedPointsAllowedPerGameStdDev(RatingSystem ratingSystem) {
        double adjStd = 0.0;
        double adjMean = getAdjustedPointsPerGame(ratingSystem);
        for (Game game : this.getGames()) {
            adjStd += Math.pow((game.getOpponentScore() - ratingSystem.getTeam(game.getOpponent()).getPointsPerGame()) - adjMean, 2);
        }
        return Math.sqrt(adjStd / numberOfGames);
    }

    public void setQuad1Rating(double quad1Rating) {
        this.quad1Rating = quad1Rating;
    }

    public void setQuad2Rating(double quad2Rating) {
        this.quad2Rating = quad2Rating;
    }

    public void setQuad3Rating(double quad3Rating) {
        this.quad3Rating = quad3Rating;
    }

    public void setQuad4Rating(double quad4Rating) {
        this.quad4Rating = quad4Rating;
    }

    public void setHomeRating(double homeRating) {
        this.homeRating = homeRating;
    }

    public void setAwayRating(double awayRating) {
        this.awayRating = awayRating;
    }

    public double getOffensiveRating() {
        return offensiveRating;
    }

    public double getDefensiveRating() {
        return defensiveRating;
    }

    public double getQuad1Rating() {
        return quad1Rating;
    }

    public double getQuad2Rating() {
        return quad2Rating;
    }

    public double getQuad3Rating() {
        return quad3Rating;
    }

    public double getQuad4Rating() {
        return quad4Rating;
    }

    public double getHomeRating() {
        return homeRating;
    }

    public double getAwayRating() {
        return awayRating;
    }

}
