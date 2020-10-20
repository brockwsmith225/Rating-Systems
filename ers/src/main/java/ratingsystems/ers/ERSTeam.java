package ratingsystems.ers;

import ratingsystems.common.interpreter.CustomTeam;
import ratingsystems.common.interpreter.Team;
import ratingsystems.ser.SERTeam;

public class ERSTeam extends SERTeam {
    private double efficiency, offensiveEfficiency, defensiveEfficiency;

    public ERSTeam(Team team) {
        super(team);
        this.team = team;
        this.offensiveEfficiency = 1.0;
        this.defensiveEfficiency = 1.0;
    }

    public void calculateEfficiency() {
        this.efficiency = Math.sqrt(this.offensiveEfficiency * this.defensiveEfficiency);
    }

    public void calculateRating() {
        team.setRating(this.getOffensiveRating() - this.getDefensiveRating());
//        team.setRating(Math.sqrt(this.offensiveEfficiency * this.defensiveEfficiency));
    }

    public double getOffensiveEfficiency() {
        return this.offensiveEfficiency;
    }

    public double getDefensiveEfficiency() {
        return this.defensiveEfficiency;
    }

    public void setOffensiveEfficiency(double offensiveEfficiency) {
        if (Double.isNaN(offensiveEfficiency)) {
            this.offensiveEfficiency = 1.0;
        } else {
            this.offensiveEfficiency = offensiveEfficiency;
        }
    }

    public void setDefensiveEfficiency(double defensiveEfficiency) {
        if (Double.isNaN(defensiveEfficiency)) {
            this.defensiveEfficiency = 1.0;
        } else {
            this.defensiveEfficiency = defensiveEfficiency;
        }
    }

}
