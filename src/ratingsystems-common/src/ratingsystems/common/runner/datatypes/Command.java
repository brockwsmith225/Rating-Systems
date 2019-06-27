package ratingsystems.common.runner.datatypes;

import java.util.HashMap;

public class Command {
    private String command;
    private String[] args;

    private HashMap<String, Boolean> options;

    public Command(String[] command) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);
    }

    public boolean getOption(String option) {
        return options.get(option);
    }
}
