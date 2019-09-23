package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;
import ratingsystems.common.cli.parameters.ParameterMap;

import java.util.List;
import java.util.Map;

public class Fetch extends Command {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, ParameterMap parameters, CommandMode commandMode) {
        if (commandMode == CommandMode.TERMINAL) {
            try {
                runner.getInterpreter(parameters.getValue("LEAGUE").toString()).fetchData((int) parameters.getValue("YEAR"));
                System.out.println("Fetched data for " + parameters.getValue("YEAR"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, ParameterMap parameters) {
        try {
            Process process = Runtime.getRuntime().exec("ping www.google.com");
            int res = process.waitFor();
            if (res != 0) {
                System.err.println("ERROR: Not connected to the internet");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
