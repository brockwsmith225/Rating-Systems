package ratingsystems.ser;

import ratingsystems.common.interpreter.CustomTeam;
import ratingsystems.common.interpreter.Team;

public class SERTeam extends CustomTeam {
    private double offensiveRating, defensiveRating;

    public SERTeam(Team team) {
        this.team = team;
        this.offensiveRating = 1.0;
        this.defensiveRating = 1.0;
    }

    public void calculateRating() {
        team.setRating(this.offensiveRating * this.defensiveRating);
    }

    public void setOffensiveRating(double offensiveRating) {
        if (Double.isNaN(offensiveRating)) {
            this.offensiveRating = 1.0;
        } else if (offensiveRating > 10.0) {
            this.offensiveRating = 10.0;
        } else {
            this.offensiveRating = offensiveRating;
        }
    }

    public void setDefensiveRating(double defensiveRating) {
        if (Double.isNaN(defensiveRating)) {
            this.defensiveRating = 1.0;
        } else if (defensiveRating > 10.0) {
            this.defensiveRating = 10.0;
        } else {
            this.defensiveRating = defensiveRating;
        }
    }

    public double getOffensiveRating() {
        return offensiveRating;
    }

    public double getDefensiveRating() {
        return defensiveRating;
    }

}
