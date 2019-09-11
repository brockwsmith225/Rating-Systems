package ratingsystems.common.cli;

import java.util.Scanner;

public class Terminal {
    private Runner runner;

    public Terminal(Runner runner) {
        this.runner = runner;
    }

    public void start() {
        System.out.print(runner.prefix + "> ");
        Scanner input = new Scanner(System.in);
        while (run(input.nextLine())) {
            System.out.print(runner.prefix + "> ");
        }
    }

    private boolean run(String comm) {
        CommandInput command = new CommandInput(split(comm, " "));

        if (runner.hasCommand(command.getCommand())) {
            runner.run(command);
        } else if (command.getCommand().equals("exit")) {
            return false;
        } else {
            System.out.println("ERROR: Command not found " + command.getCommand());
        }
        return true;
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
        StringBuilder result = new StringBuilder();
        result.append(string);
        result.append(spaces(columns - string.length()));
        return result.toString();
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
        StringBuilder result = new StringBuilder();
        result.append(spaces(columns - string.length()));
        result.append(string);
        return result.toString();
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
        StringBuilder result = new StringBuilder();
        int left = (int)Math.ceil((columns - string.length()) / 2.0);
        int right = (columns - string.length()) - left;
        result.append(spaces(left));
        result.append(string);
        result.append(spaces(right));
        return result.toString();
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
