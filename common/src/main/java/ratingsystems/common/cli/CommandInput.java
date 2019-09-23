package ratingsystems.common.cli;

import ratingsystems.common.cli.parameters.IntegerParameter;
import ratingsystems.common.cli.parameters.Parameter;
import ratingsystems.common.cli.parameters.ParameterMap;
import ratingsystems.common.cli.parameters.StringParameter;

import java.util.*;

public class CommandInput {
    public static Runner runner;

    private String command;
    private List<String> args;
    private HashMap<Character, String> optionLetterToName;
    private HashMap<String, Boolean> flags;
    private ParameterMap availableParameters;
    private ParameterMap parameters;

    public CommandInput() {
        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "CLEAN");
        this.optionLetterToName.put('p', "PRETTY_PRINT");
        this.optionLetterToName.put('w', "WEEK");
        this.optionLetterToName.put('y', "YEAR");
        this.optionLetterToName.put('l', "LEAGUE");
        this.optionLetterToName.put('s', "START_YEAR");

        this.availableParameters = new ParameterMap();
        this.availableParameters.put("YEAR", new IntegerParameter(2019, 1800, 2500));
        this.availableParameters.put("WEEK", new IntegerParameter(16, 0, 50));
        this.availableParameters.put("LEAGUE", new StringParameter("cfb", runner.getLeagues()));
        this.availableParameters.put("START_YEAR", new IntegerParameter(2014, 1800, 2500));

        this.flags = new HashMap<>();
        this.flags.put("CLEAN", false);
        this.flags.put("PRETTY_PRINT", false);

        this.parameters = new ParameterMap();
        this.parameters.put("YEAR", availableParameters.get("YEAR"));
        this.parameters.put("LEAGUE", availableParameters.get("LEAGUE"));

        this.args = new ArrayList<>();
    }

    public CommandInput(String command) {
        this.flags = new HashMap<>();
        this.flags.put("clean", false);
        this.flags.put("pretty-print", false);
        this.flags.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>();
    }

    public CommandInput(String command, List<String> args) {
        this.flags = new HashMap<>();
        this.flags.put("clean", false);
        this.flags.put("pretty-print", false);
        this.flags.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>(args);
    }

    public CommandInput(String command, Map<String, Boolean> options) {
        this.flags = new HashMap<>();
        this.flags.put("clean", false);
        this.flags.put("pretty-print", false);
        this.flags.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>();
        for (String option : options.keySet()) {
            if (this.flags.containsKey(option)) {
                this.flags.put(option, options.get(option));
            }
        }
    }

    public CommandInput(String command, List<String> args, Map<String, Boolean> options) {
        this.flags = new HashMap<>();
        this.flags.put("clean", false);
        this.flags.put("pretty-print", false);
        this.flags.put("week", false);

        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "clean");
        this.optionLetterToName.put('p', "pretty-print");
        this.optionLetterToName.put('w', "week");

        this.command = command;
        this.args = new ArrayList<>(args);
        for (String option : options.keySet()) {
            if (this.flags.containsKey(option)) {
                this.flags.put(option, options.get(option));
            }
        }
    }

    public CommandInput(String[] command) {
        this();

        this.command = command[0];
        String parameter = "";
        for (int i = 1; i < command.length; i++) {
            String c = command[i];
            if (c.startsWith("-")) {
                for (char o : c.toCharArray()) {
                    if (this.optionLetterToName.containsKey(o)) {
                        String option = this.optionLetterToName.get(o);
                        if (this.flags.containsKey(option)) {
                            this.flags.put(option, true);
                            parameter = "";
                        } else if (this.availableParameters.containsKey(option)) {
                            parameter = option;
                        }
                    } else if (o != '-') {
                        parameter = "";
                        System.err.println("WARNING: Option " + o + " not found, skipping");
                    }
                }
            } else {
                if (!parameter.isEmpty()) {
                    Parameter p = this.availableParameters.get(parameter).copy();
                    if (p.validateValue(c)) {
                        p.setValue(c);
                    }
                } else {
                    this.args.add(c);
                }
                parameter = "";
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

    public HashMap<String, Boolean> getOptions() {
        return flags;
    }

    public ParameterMap getParameters() {
        return parameters;
    }
}
