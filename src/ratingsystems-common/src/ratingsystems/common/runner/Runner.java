package ratingsystems.common.runner;

import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.runner.datatypes.Command;

import java.util.HashMap;
import java.util.function.Function;

public abstract class Runner {
    protected HashMap<String, Function<Command, Boolean>> commands = new HashMap<>();
    protected Interpreter interpreter;
    protected Command command;

    public Runner() {}

    public Runner(String[] command) {
        //rrs rank
        //rrs [options] <command> [arguments]
        //options: -c, --clean clean
        //         -p, --pretty pretty-print

    }

    abstract void loadRatingSystem();
}
