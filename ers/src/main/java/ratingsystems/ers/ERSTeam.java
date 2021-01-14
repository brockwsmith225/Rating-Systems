package ratingsystems.ers;

import ratingsystems.common.interpreter.CustomTeam;
import ratingsystems.common.interpreter.Team;
import ratingsystems.sdr.SDRTeam;
import ratingsystems.ser.SERTeam;

public class ERSTeam extends SDRTeam {
    public ERSTeam(Team team) {
        super(team);
        this.team = team;
    }
}
