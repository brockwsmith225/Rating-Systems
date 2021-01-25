package ratingsystems.eps;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;

public class EPSTeam extends Team {
    private double offensiveRating, defensiveRating;
    private double quad1Rating, quad2Rating, quad3Rating, quad4Rating;
    private double homeRating, awayRating;

    public EPSTeam(Team team) {
        super(team);
    }

//    public void calculateRating() {
//        team.setRating(getRatingPoints(1));
//    }

    public double getRatingPoints(double ppg) {
        return (this.getRating() - 1) * ppg;
        //return getOffensiveRatingPoints(ppg) - getDefensiveRatingPoints(ppg);
    }

    public double getOffensiveRatingPoints(double ppg) {
        return (offensiveRating - 1) * ppg;
    }

    public double getDefensiveRatingPoints(double ppg) {
        return (1 - defensiveRating) * ppg;
    }

    public void calculateRating() {
        this.setRating(this.offensiveRating - this.defensiveRating);
    }

    public void setOffensiveRating(double offensiveRating) {
        if (Double.isNaN(offensiveRating)) {
            this.offensiveRating = 0.0;
        } else {
            this.offensiveRating = offensiveRating;
        }
    }

    public void setDefensiveRating(double defensiveRating) {
        if (Double.isNaN(defensiveRating)) {
            this.defensiveRating = 0.0;
        } else {
            this.defensiveRating = defensiveRating;
        }
    }

    public double getPointsPerGameStDev() {
        double std = 0.0;
        double mean = this.getPointsPerGame();
        for (Game game : this.getGames()) {
            std += Math.pow(game.getScore() - mean, 2);
        }
        return Math.sqrt(std / this.getNumberOfGames());
    }

    public double getPointsAllowedPerGameStDev() {
        double std = 0.0;
        double mean = this.getPointsAllowedPerGame();
        for (Game game : this.getGames()) {
            std += Math.pow(game.getOpponentScore() - mean, 2);
        }
        return Math.sqrt(std / this.getNumberOfGames());
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
