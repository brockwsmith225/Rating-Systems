package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.interpreter.Interpreter;

public abstract class Command {

    /**
     * Runs the command
     *
     * @param runner the runner to get necessary parameters from
     * @param commandInput the command input from the user
     */
    abstract public void run(Runner runner, CommandInput commandInput);

    /**
     * Validates that the input from the user will be enough to run the command
     *
     * @param runner the runner to get necessary parameters from
     * @param commandInput the command input from the user
     * @return true if the input from the user is enough to run the command, false otherwise
     */
    abstract public boolean validateInput(Runner runner, CommandInput commandInput);



    //========== Common validation checks ==========

    /**
     * Checks that there is data for the current league for the current year
     *
     * @param runner
     * @return true if there is data for the current league for the current year, false otherwise
     */
    protected static boolean validateDataExists(Runner runner) {
        Interpreter interpreter = runner.getInterpreter((String)runner.getParameter("LEAGUE"));

        if (!interpreter.hasData((Integer)runner.getParameter("YEAR"))) {
            System.err.println("ERROR: Data not found for league " + runner.getParameter("LEAGUE") + " and year "
                    + runner.getParameter("YEAR"));
            return false;
        }
        return true;
    }

    protected static boolean validateArgsExist(CommandInput commandInput, int requiredArgs) {
        if (!commandInput.hasArgs(requiredArgs)) {
            System.err.println("ERROR: " + requiredArgs + " arguments required for " + commandInput.getCommand()
                    + ", " + commandInput.getArgs() + " found");
            return false;
        }
        return true;
    }
}
