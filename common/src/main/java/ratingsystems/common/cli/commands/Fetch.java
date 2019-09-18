package ratingsystems.common.cli.commands;

import ratingsystems.common.cli.CommandInput;
import ratingsystems.common.cli.Runner;

public class Fetch extends Command {
    @Override
    public Object run(Runner runner, CommandInput commandInput, CommandMode commandMode) {
        try {
            runner.getInterpreter(runner.getParameterValue("LEAGUE").toString()).fetchData((int) runner.getParameterValue("YEAR"));
            return "Fetched data for " + runner.getParameterValue("YEAR");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean validateInput(Runner runner, CommandInput commandInput) {
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
