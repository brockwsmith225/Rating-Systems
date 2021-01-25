package ratingsystems.eps;

import ratingsystems.common.interpreter.Team;
import ratingsystems.ser.SERTeam;

public class EPSTeam extends SERTeam {
    public EPSTeam(Team team) {
        super(team);
        this.team = team;
    }

//    public void calculateRating() {
//        team.setRating(getRatingPoints(1));
//    }

    public double getRatingPoints(double ppg) {
        return (this.getRating() - 1) * ppg;
        //return getOffensiveRatingPoints(ppg) - getDefensiveRatingPoints(ppg);
    }

    public double getOffensiveRatingPoints(double ppg) {
        return (this.getOffensiveRating() - 1) * ppg;
    }

    public double getDefensiveRatingPoints(double ppg) {
        return (1 - this.getDefensiveRating()) * ppg;
    }

}
