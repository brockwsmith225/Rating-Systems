package ratingsystems.rrs;

import ratingsystems.common.interpreter.CustomTeam;
import ratingsystems.common.interpreter.Team;

public class RRSTeam extends CustomTeam {

    private double positiveRating, negativeRating;

    public RRSTeam(Team team) {
        this.team = team;
        this.positiveRating = 0;
        this.negativeRating = 0;
    }

    public void calculateRating() {
        team.setRating(this.positiveRating - this.negativeRating);
    }

    public void setPositiveRating(double positiveRating) {
        this.positiveRating = positiveRating;
    }

    public void setNegativeRating(double negativeRating) {
        this.negativeRating = negativeRating;
    }

    public double getPositiveRating() {
        return positiveRating;
    }

    public double getNegativeRating() {
        return negativeRating;
    }

}
