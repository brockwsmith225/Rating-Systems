package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.parameters.Parameters;

import java.util.List;
import java.util.Map;

public class Fetch extends Command {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
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
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
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
