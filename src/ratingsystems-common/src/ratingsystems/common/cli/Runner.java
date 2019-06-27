package ratingsystems.common.cli;

import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.common.cli.datatypes.Command;

import java.util.HashMap;
import java.util.function.Function;

public class Runner {
    public String prefix;

    protected HashMap<String, Function<Command, Boolean>> commands = new HashMap<>();
    protected RatingSystem ratingSystem;

    public Runner() {
        prefix = "";
        //Add general rating system commands here
        commands.put("rank", (Command options) -> {
            System.out.println();
            ratingSystem.printTeamRankings();
            System.out.println();
            return true;
        });
        commands.put("rank-confs", (Command options) -> {
            return false;
        });
    }

    public boolean run(Command command) {
        return commands.get(command.getCommand()).apply(command);
    }

    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }
}
