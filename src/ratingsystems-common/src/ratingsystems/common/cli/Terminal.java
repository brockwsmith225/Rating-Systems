package ratingsystems.common.cli;

import java.util.Scanner;

public class Terminal {
    private Runner runner;

    public Terminal(Runner runner) {
        this.runner = runner;
    }

    public void start() {
        System.out.print(runner.prefix + "$: ");
        Scanner input = new Scanner(System.in);
        run(input.nextLine());
    }

    public void run(String comm) {
        CommandInput command = new CommandInput(split(comm, " "));

        if (runner.hasCommand(command.getCommand())) {
            runner.run(command);
        } else if (command.getCommand().equals("exit")) {
            return;
        } else {
            System.out.println("ERROR: Command not found " + command.getCommand());
        }

        start();
    }

    /**
     * Splits the inputted string by the inputted delimiter. Ignores
     * portions of the inputted string that are within quotes
     *
     * @param input the input to be split
     * @return the split input
     */
    public static String[] split(String input, String delimiter) {
        String[] res = input.split(delimiter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replace("\"", "");
        }
        return res;
    }
}
