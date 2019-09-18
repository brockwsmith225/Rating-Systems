package ratingsystems.common.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandInput {
    private String command;
    private List<String> args;

    private HashMap<String, Boolean> options;
    private HashMap<Character, String> optionLetterToName;

    public CommandInput(String command) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);
        this.options.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>();
    }

    public CommandInput(String command, List<String> args) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);
        this.options.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>(args);
    }

    public CommandInput(String command, Map<String, Boolean> options) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);
        this.options.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>();
        for (String option : options.keySet()) {
            if (this.options.containsKey(option)) {
                this.options.put(option, options.get(option));
            }
        }
    }

    public CommandInput(String command, List<String> args, Map<String, Boolean> options) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);
        this.options.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>(args);
        for (String option : options.keySet()) {
            if (this.options.containsKey(option)) {
                this.options.put(option, options.get(option));
            }
        }
    }

    public CommandInput(String[] command) {
        this.options = new HashMap<>();
        this.options.put("clean", false);
        this.options.put("pretty-print", false);
        this.options.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.args = new ArrayList<>();

        boolean commandFound = false;
        for (String c : command) {
            if (c.startsWith("-")) {
                for (char o : c.toCharArray()) {
                    if (this.optionLetterToName.containsKey(o)) {
                        this.options.put(this.optionLetterToName.get(o), true);
                    } else if (o != '-') {
                        System.err.println("WARNING: Option " + o + " not found, skipping");
                    }
                }
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
