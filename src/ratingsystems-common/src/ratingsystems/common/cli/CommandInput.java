package ratingsystems.common.cli;

import java.util.HashMap;
import java.util.List;

public class CommandInput {
    private String command;
    private List<String> args;

    private HashMap<String, Boolean> options;

    public CommandInput(String[] command) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);

        boolean commandFound = false;
        for (String c : command) {
            if (c.startsWith("-")) {

            } else {
                if (!commandFound) {
                    this.command = c;
                    commandFound = true;
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

    public boolean hasArgs(int n) {
        return args.size() >= n;
    }

    public boolean getOption(String option) {
        return options.get(option);
    }
}
