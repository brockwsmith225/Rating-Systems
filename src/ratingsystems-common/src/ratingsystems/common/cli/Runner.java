package ratingsystems.common.cli;

import ratingsystems.common.cli.commands.Command;
import ratingsystems.common.cli.commands.Predict;
import ratingsystems.common.cli.commands.Rank;
import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.HashMap;

public abstract class Runner {
    public String prefix;

    protected HashMap<String, Interpreter> interpreters;
    protected HashMap<String, Parameter> parameters;
    protected HashMap<String, Command> commands;
    protected RatingSystem ratingSystem;

    public Runner() {
        prefix = "";

        //Add interpreters here
        interpreters = new HashMap<>();
        interpreters.put("cfb", new CollegeFootballInterpreter());

        //Add general rating system parameters here
        parameters = new HashMap<>();
        parameters.put("YEAR", new Parameter(2018, 1800, 2500));
        parameters.put("WEEK", new Parameter(50, 0, 50));
        parameters.put("LEAGUE", new Parameter("cfb", interpreters.keySet()));

        //Add general rating system commands here
        commands = new HashMap<>();
        commands.put("rank", new Rank());
        commands.put("predict", new Predict());
    }

    public void run(CommandInput command) {
        commands.get(command.getCommand()).run(this, command);
    }

    public Object getParameter(String parameter) {
        return parameters.get(parameter).getValue();
    }

    public Interpreter getInterpreter(String interpreter) {
        return interpreters.get(interpreter);
    }

    abstract public RatingSystem loadRatingSystem();

    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }
}
