package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.parameters.ParameterMap;
import ratingsystems.common.interpreter.Interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command {

    /**
     * Runs the command
     *
     * @param runner the runner to get necessary parameters from
     */
    abstract public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, ParameterMap parameters, CommandMode commandMode);

    /**
     * Validates that the input from the user will be enough to run the command
     *
     * @param runner the runner to get necessary parameters from
     * @return true if the input from the user is enough to run the command, false otherwise
     */
    abstract public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, ParameterMap parameters);



    //========== Common validation checks ==========

    /**
     * Checks that there is data for the current league for the current year
     *
     * @param runner
     * @return true if there is data for the current league for the current year, false otherwise
     */
    protected static boolean validateDataExists(Runner runner, ParameterMap parameters) {
        Interpreter interpreter = runner.getInterpreter((String)parameters.getValue("LEAGUE"));

        if (!interpreter.hasData((Integer)parameters.getValue("YEAR"))) {
            System.err.println("ERROR: Data not found for league " + parameters.getValue("LEAGUE") + " and year "
                    + parameters.getValue("YEAR"));
            return false;
        }
        return true;
    }

    protected static boolean validateArgsExist(List<String> arguments, int requiredArgs) {
        if (arguments.size() < requiredArgs) {
            System.err.println("ERROR: " + requiredArgs + " arguments required, " + arguments.size() + " found");
            return false;
        }
        return true;
    }
}
