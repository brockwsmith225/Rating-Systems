package ratingsystems.common.cli;

import ratingsystems.common.cli.commands.CommandMode;
import ratingsystems.common.cli.parameters.Parameters;

import java.util.Scanner;

public class Terminal {
    private Runner runner;

    public Terminal(Runner runner) {
        this.runner = runner;
        Parameters.leagues = runner.getLeagues();
    }

    public void start() {
        System.out.print(runner.prefix + "> ");
        Scanner input = new Scanner(System.in);
        while (run(input.nextLine())) {
            System.out.print(runner.prefix + "> ");
        }
    }

    public boolean run(String command) {
        CommandInput commandInput = new CommandInput(command);

        if (runner.hasCommand(commandInput.getCommand())) {
            runner.run(commandInput.getCommand(), commandInput.getArgs(), commandInput.getOptions(), commandInput.getParameters(), CommandMode.TERMINAL);
        } else if (commandInput.getCommand().equals("exit")) {
            return false;
        } else {
            System.out.println("ERROR: Command not found " + commandInput.getCommand());
        }
        return true;
    }

    /**
     * Left justifies a string in a section of a specified width
     *
     * @param string string to be formatted
     * @param columns width of section in number of characters
     * @return a formatted version of the inputted string
     */
    public static String leftJustify(String string, int columns) {
        if (string.length() > columns) {
            return string.substring(0, columns - 3) + "...";
        }
        return string + spaces(columns - string.length());
    }

    /**
     * Right justifies a string in a section of a specified width
     *
     * @param string string to be formatted
     * @param columns width of section in number of characters
     * @return a formatted version of the inputted string
     */
    public static String rightJustify(String string, int columns) {
        if (string.length() > columns) {
            return string.substring(0, columns - 3) + "...";
        }
        return spaces(columns - string.length()) + string;
    }

    /**
     * Center justifies a string in a section of a specified width
     *
     * @param string string to be formatted
     * @param columns width of section in number of characters
     * @return a formatted version of the inputted string
     */
    public static String centerJustify(String string, int columns) {
        if (string.length() > columns) {
            return string.substring(0, columns - 3) + "...";
        }
        int left = (int)Math.ceil((columns - string.length()) / 2.0);
        int right = (columns - string.length()) - left;
        return spaces(left) + string + spaces(right);
    }

    public static String round(double number, int places) {
        double pow10 = Math.pow(10, places);
        StringBuilder result = new StringBuilder(Double.toString(Math.round(number * pow10) / pow10));
        if (!result.toString().contains(".")) result.append(".");
        while (result.toString().split("\\.")[1].length() < places) {
            result.append("0");
        }
        return result.toString();
    }

    /**
     * Returns a string with the specified number of spaces
     *
     * @param number the number of spaces
     * @return a string with the specified number of spaces
     */
    private static String spaces(int number) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < number; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
    }
}
