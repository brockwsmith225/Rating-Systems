package ratingsystems.common.commands;

import ratingsystems.common.Runner;
import ratingsystems.common.interpreter.Bracket;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class EvaluateBracket extends Command {
    @Override
    public Object run(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        RatingSystem ratingSystem = runner.loadRatingSystem(options, parameters);
        Interpreter interpreter = runner.getInterpreter((String)parameters.getValue("LEAGUE"));;
        if (commandMode == CommandMode.TERMINAL) {
            try {
                Bracket bracket = interpreter.parseBracket((Integer) parameters.getValue("YEAR"));
                bracket.evaluate(ratingSystem);
                Map<String, List<Double>> odds = bracket.getFullOdds();
                Map<String, List<String>> bracketNames = bracket.getBracketNames();
                for (String team : odds.keySet()) {
                    System.out.print(team + "\t");
                    for (String bracketName : bracketNames.get(team)) {
                        System.out.print(bracketName + "\t");
                    }
                    for (Double o : odds.get(team)) {
                        System.out.print(o + "\t");
                    }
                    System.out.println();
                }
            } catch (FileNotFoundException e) {}
        }
        return null;
    }

    @Override
    public boolean validateInput(Runner runner, List<String> arguments, Map<String, Boolean> options, Parameters parameters) {
        if (!Command.validateDataExists(runner, parameters)) return false;

        Interpreter interpreter = runner.getInterpreter((String)parameters.getValue("LEAGUE"));
        if (!interpreter.hasBracket((Integer)parameters.getValue("YEAR"))) {
            System.err.println("ERROR: Bracket not found for league " + parameters.getValue("LEAGUE") + " and year "
                    + parameters.getValue("YEAR"));
            return false;
        }

        return true;
    }
}
