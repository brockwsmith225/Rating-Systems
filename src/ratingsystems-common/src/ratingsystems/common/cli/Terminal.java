package ratingsystems.common.cli;

import java.util.Scanner;

public class Terminal {
    private Runner runner;

    public Terminal(Runner runner) {
        this.runner = runner;
    }

    public void start() {
        System.out.print(runner.getPrefix() + " > ");
        Scanner input = new Scanner(System.in);
    }
}
