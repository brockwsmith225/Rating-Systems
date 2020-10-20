package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.parameters.Parameters;

import java.util.List;
import java.util.Map;

public class Set extends Command<Runner> {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        if (commandMode == CommandMode.TERMINAL) {
            String parameter = arguments.get(0).toUpperCase();
            String value = arguments.get(1);
            if (Parameters.setDefaultParameterValue(parameter, value)) {
                return "Set parameter " + parameter + " to " + value;
            } else {
                return "ERROR: invalid value " + value + " for parameter " + parameter;
            }
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        if (!Command.validateArgsExist(arguments, 2)) return false;

        String parameter = arguments.get(0).toUpperCase();
        String value = arguments.get(1);
        if (!Parameters.isValidParameter(parameter)) {
            System.err.println("ERROR: No parameter found, " + parameter);
            return false;
        }
        if (!Parameters.isValidParameterValue(parameter, value)) {
            System.err.println("ERROR: invalid value " + value + " for parameter " + parameter);
            return false;
        }

        return true;
    }
}
