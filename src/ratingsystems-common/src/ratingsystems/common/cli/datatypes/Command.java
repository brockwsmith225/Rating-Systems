package ratingsystems.common.cli.datatypes;

import java.util.HashMap;
import java.util.List;

public class Command {
    private String command;
    private List<String> args;

    private HashMap<String, Boolean> options;

    public Command(String[] command) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);

        boolean commandFound = false;
        for (String c : command) {
            if (c.startsWith("-")) {

            } else {
                if (!commandFound) {
                    this.command = c;
                } else {
                    this.args.add(c);
                }
            }
        }
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    public boolean getOption(String option) {
        return options.get(option);
    }
}
