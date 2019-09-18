package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;

import java.util.List;

public class Set extends Command {
    @Override
    public String run(Runner runner, CommandInput commandInput) {
        List<String> args = commandInput.getArgs();
        String parameter = args.get(0).toUpperCase();
        String value = args.get(1);
        if (runner.getParameter(parameter).getType().equals(String.class)) {
            runner.getParameter(parameter).setValue(value);
        } else if (runner.getParameter(parameter).getType().equals(Integer.class)) {
            int integerValue = Integer.parseInt(value);
            runner.getParameter(parameter).setValue(integerValue);
        }
        return "Set parameter " + parameter + " to " + value;
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
        if (!Command.validateArgsExist(commandInput, 2)) return false;

        List<String> args = commandInput.getArgs();
        String parameter = args.get(0).toUpperCase();
        String value = args.get(1);
        if (!runner.parameterSet().contains(parameter)) {
            System.err.println("ERROR: No parameter found, " + parameter);
            return false;
        }
        if (runner.getParameter(parameter).getType().equals(String.class)) {
            if (!runner.getParameter(parameter).validateValue(value)) {
                System.err.println("ERROR: Invalid value " + value + " for parameter " + parameter);
                return false;
            }
        } else if (runner.getParameter(parameter).getType().equals(Integer.class)) {
            for (char c : value.toCharArray()) {
                if (c < '0' || c > '9') {
                    System.err.println("ERROR: Invalid value " + value + " for parameter " + parameter);
                    return false;
                }
            }
            int integerValue = Integer.parseInt(value);
            if (!runner.getParameter(parameter).validateValue(integerValue)) {
                System.err.println("ERROR: Invalid value " + value + " for parameter " + parameter);
                return false;
            }
        }

        return true;
    }
}
