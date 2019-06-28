package ratingsystems.common.cli;

import ratingsystems.common.cli.commands.Command;
import ratingsystems.common.cli.commands.Rank;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.HashMap;

public abstract class Runner {
    public String prefix;

    protected HashMap<String, Object> parameters;
    protected HashMap<String, Command> commands;
    protected RatingSystem ratingSystem;

    public Runner() {
        prefix = "";

        //Add general rating system parameters here
        parameters = new HashMap<>();

        //Add general rating system commands here
        commands = new HashMap<>();
        commands.put("rank", new Rank());
    }

    public void run(CommandInput command) {
        commands.get(command.getCommand()).run(this, command);
    }

    public Object getParameter(String parameter) {
        return parameters.get(parameter);
    }

    public Interpreter getInterpreter(String interpreter) {
        return null;
    }

    abstract public RatingSystem loadRatingSystem();

    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }
}
