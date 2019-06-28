package ratingsystems.common.cli;

import ratingsystems.common.ratingsystem.RatingSystem;

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
        commands.put("predict", (Command options) -> {
            if (!options.hasArgs(2)) {
                System.out.println("ERROR: 2 arguments required for predict, " + options.getArgs().size() + " found");
                return false;
            }
            System.out.println(ratingSystem.predictGame(options.getArgs().get(0), options.getArgs().get(1)));
            return true;
        });
    }

    public boolean run(Command command) {
        return commands.get(command.getCommand()).apply(command);
    }

    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }
}
