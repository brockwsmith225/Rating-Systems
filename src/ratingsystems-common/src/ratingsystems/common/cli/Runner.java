package ratingsystems.common.cli;

import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.common.cli.datatypes.Command;

import java.util.HashMap;
import java.util.function.Function;

public abstract class Runner {
    private static final String PREFIX = "";

    protected HashMap<String, Function<Command, Boolean>> commands = new HashMap<>();
    protected RatingSystem ratingSystem;

    public Runner() {
        //Add general rating system commands here
        commands.put("rank", (Command options) -> {
            ratingSystem.printTeamRankings();
            return true;
        });
        commands.put("rank-confs", (Command options) -> {
            return false;
        });
    }

    public String getPrefix() {
        return PREFIX;
    }
}
