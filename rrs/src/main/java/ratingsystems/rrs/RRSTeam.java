package ratingsystems.rrs;

import ratingsystems.common.interpreter.Team;

public class RRSTeam extends Team {

    private double positiveRating, negativeRating;

    public RRSTeam(Team team) {
        super(team);
        this.positiveRating = 0;
        this.negativeRating = 0;
    }

    public void calculateRating() {
        this.setRating(this.positiveRating - this.negativeRating);
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
