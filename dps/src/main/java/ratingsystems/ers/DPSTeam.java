package ratingsystems.ers;

import ratingsystems.common.interpreter.CustomTeam;
import ratingsystems.common.interpreter.Team;
import ratingsystems.sdr.SDRTeam;
import ratingsystems.ser.SERTeam;

public class DPSTeam extends SDRTeam {
    public DPSTeam(Team team) {
        super(team);
        this.team = team;
    }
}
