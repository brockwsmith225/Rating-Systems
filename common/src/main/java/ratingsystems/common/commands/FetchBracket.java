package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.parameters.Parameters;

import java.util.List;
import java.util.Map;

public class FetchBracket extends Command<Runner> {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        if (commandMode == CommandMode.TERMINAL) {
            try {
                runner.getInterpreter(parameters.getValue("LEAGUE").toString()).fetchBracket((int) parameters.getValue("YEAR"));
                System.out.println("Fetched bracket for " + parameters.getValue("YEAR"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        try {
            Process process = Runtime.getRuntime().exec("ping 8.8.8.8");
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
