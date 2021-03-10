package ratingsystems.common;

import ratingsystems.common.collegebasketball.CollegeBasketballConferencesInterpreter;
import ratingsystems.common.collegefootball.CollegeFootballCoachSchoolInterpreter;
import ratingsystems.common.collegefootball.CollegeFootballCoachesInterpreter;
import ratingsystems.common.collegefootball.CollegeFootballConferencesInterpreter;
import ratingsystems.common.commands.*;
import ratingsystems.common.parameters.Parameters;
import ratingsystems.common.collegebasketball.CollegeBasketballInterpreter;
import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Runner<T extends RatingSystem> {
    public String prefix;

    protected HashMap<String, Interpreter> interpreters;
    protected HashMap<String, Command> commands;
    protected HashMap<Parameters, T> ratingSystems;

    /**
     * Creates a new Runner object
     */
    public Runner() {
        prefix = "";

        //Add interpreters here
        interpreters = new HashMap<>();
        interpreters.put("cfb", new CollegeFootballInterpreter());
        interpreters.put("cfb-coaches", new CollegeFootballCoachesInterpreter());
        interpreters.put("cfb-conferences", new CollegeFootballConferencesInterpreter());
        interpreters.put("cfb-coach-school", new CollegeFootballCoachSchoolInterpreter());
        interpreters.put("cbb", new CollegeBasketballInterpreter());
        interpreters.put("cbb-conferences", new CollegeBasketballConferencesInterpreter());

        //Add general rating system commands here
        commands = new HashMap<>();
        commands.put("check-predictions", new CheckPredictions());
        commands.put("fetch", new Fetch());
        commands.put("predict", new Predict());
        commands.put("play", new Play());
        commands.put("rank", new Rank());
        commands.put("standings", new Standings());
        commands.put("stats", new Stats());
        commands.put("predictions", new Predictions());
        commands.put("bracket", new EvaluateBracket());
        commands.put("fetch-bracket", new FetchBracket());
//        commands.put("set", new ratingsystems.common.commands.Set());

        ratingSystems = new HashMap<>();
    }

    /**
     * Runs a given command based on the user's input
     *
     * @param command the command the user inputted into the terminal
     */
    public Object run(String command, List<String> arguments, Map<String, Boolean> options, Parameters parameters, CommandMode commandMode) {
        if (commands.get(command).validateInput(this, arguments, options, parameters)) {
            return commands.get(command).run(this, arguments, options, parameters, commandMode);
        }
        return null;
    }

    /**
     * Returns the interpreter specified by the league name
     *
     * @param interpreter the name of the league whose interpreter to return
     * @return the interpreter specified by the league name
     */
    public Interpreter getInterpreter(String interpreter) {
        return interpreters.get(interpreter);
    }

    /**
     * Returns a rating system based on the input of a specified command and a specified week
     *
     * Uses rating systems already created in the past to improve performance for multiple commands
     * run on the same parameters
     *
     * @return the rating system needed for the given command
     */
    public T loadRatingSystem(Map<String, Boolean> options, Parameters parameters) {
        boolean cleanFlag = options.get("CLEAN");

        if (cleanFlag || !ratingSystems.containsKey(parameters)) {
            ratingSystems.put(parameters, loadNewRatingSystem(options, parameters));
        }
        return ratingSystems.get(parameters);
    }

    /**
     * Returns a rating system based on the input of a specified command and a specified week
     *
     * Uses rating systems already created in the past to improve performance for multiple commands
     * run on the same parameters
     *
     * @param week the week of the rating system to return
     * @return the rating system needed for the given command
     */
    public T loadRatingSystem(Map<String, Boolean> options, Parameters parameters, int week) {
        boolean cleanFlag = options.get("CLEAN");

        parameters.setParameterValue("WEEK", week);

        if (cleanFlag || !ratingSystems.containsKey(parameters)) {
            ratingSystems.put(parameters, loadNewRatingSystem(options, parameters));
        }
        return ratingSystems.get(parameters);
    }

    public abstract T loadNewRatingSystem(Map<String, Boolean> options, Parameters parameters);

    /**
     * Loads a new rating system with the given interpreter and year
     *
     * @param interpreter the interpreter with which to create the rating system
     * @param year the year for which to create the rating system
     * @return a new rating system
     */
    @Deprecated
    public abstract T loadNewRatingSystem(Interpreter interpreter, int year);

    /**
     * Loads a new rating system with the given interpreter, year, and week
     *
     * @param interpreter the interpreter with which to create the rating system
     * @param year the year for which to create the rating system
     * @param week the week for which to create the rating system
     * @return a new rating system
     */
    @Deprecated
    public abstract T loadNewRatingSystem(Interpreter interpreter, int year, int week);

    public java.util.Set<String> getLeagues() {
        return interpreters.keySet();
    }

    /**
     * Checks that the command is valid
     *
     * @param command the command to check
     * @return true if the command exists, false otherwise
     */
    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }
}
