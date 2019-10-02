package ratingsystems.common.cli;

import ratingsystems.common.parameters.Parameters;

import java.util.*;

public class CommandInput {
    private String command;
    private List<String> args;
    private HashMap<Character, String> optionLetterToName;
    private HashMap<String, Boolean> flags;
    private Parameters parameters;

    public CommandInput() {
        this.optionLetterToName = new HashMap<>();
        this.optionLetterToName.put('c', "CLEAN");
        this.optionLetterToName.put('p', "PRETTY_PRINT");
        this.optionLetterToName.put('w', "WEEK");
        this.optionLetterToName.put('y', "YEAR");
        this.optionLetterToName.put('l', "LEAGUE");
        this.optionLetterToName.put('s', "START_YEAR");
        this.optionLetterToName.put('o', "LOCATION");

        this.flags = new HashMap<>();
        this.flags.put("CLEAN", false);
        this.flags.put("PRETTY_PRINT", false);

        this.parameters = new Parameters();

        this.args = new ArrayList<>();
    }

    public CommandInput(String command) {
        this(split(command, " "));
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
                        } else if (Parameters.isValidParameter(option)) {
                            parameter = option;
                        }
                    } else if (o != '-') {
                        parameter = "";
                        System.err.println("WARNING: Option " + o + " not found, skipping");
                    }
                }
            } else {
                if (!parameter.isEmpty()) {
                    parameters.setParameterValue(parameter, c);
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

    public Parameters getParameters() {
        return parameters;
    }

    /**
     * Splits the inputted string by the inputted delimiter. Ignores
     * portions of the inputted string that are within quotes
     *
     * @param input the input to be split
     * @return the split input
     */
    private static String[] split(String input, String delimiter) {
        String[] res = input.split(delimiter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replace("\"", "");
        }
        return res;
    }
}
