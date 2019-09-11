package ratingsystems.common.cli;

import ratingsystems.common.cli.commands.*;
import ratingsystems.common.collegebasketball.CollegeBasketballInterpreter;
import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.util.HashMap;
import java.util.Set;

public abstract class Runner {
    public String prefix;

    protected HashMap<String, Interpreter> interpreters;
    protected HashMap<String, Parameter> parameters;
    protected HashMap<String, Command> commands;
    protected HashMap<String, HashMap<Integer, HashMap<Integer, RatingSystem>>> ratingSystems; //league, year, week

    /**
     * Creates a new Runner object
     */
    public Runner() {
        prefix = "";

        //Add interpreters here
        interpreters = new HashMap<>();
        interpreters.put("cfb", new CollegeFootballInterpreter());
        interpreters.put("cbb", new CollegeBasketballInterpreter());

        //Add general rating system parameters here
        parameters = new HashMap<>();
        parameters.put("YEAR", new Parameter(Integer.class, 2019, 1800, 2500));
        parameters.put("WEEK", new Parameter(Integer.class, 16, 0, 50));
        parameters.put("LEAGUE", new Parameter(String.class, "cfb", interpreters.keySet()));

        //Add general rating system commands here
        commands = new HashMap<>();
        commands.put("check-predictions", new CheckPredictions());
        commands.put("fetch", new Fetch());
        commands.put("predict", new Predict());
        commands.put("rank", new Rank());
        commands.put("set", new ratingsystems.common.cli.commands.Set());

        ratingSystems = new HashMap<>();
    }

    /**
     * Runs a given command based on the user's input
     *
     * @param command the command the user inputted into the terminal
     */
    public void run(CommandInput command) {
        if (commands.get(command.getCommand()).validateInput(this, command)) {
            commands.get(command.getCommand()).run(this, command);
        }
    }

    /**
     * Returns the parameter specified by the parameter name
     *
     * @param parameter the name of the parameter to return
     * @return the parameter specified by the parameter name
     */
    public Parameter getParameter(String parameter) {
        return parameters.get(parameter);
    }

    /**
     * Returns the value of the paramater specified by the parameter name
     *
     * @param parameter the name of the parameter whose value to return
     * @return the value of the parameter specified by the parameter name
     */
    public Object getParameterValue(String parameter) {
        return parameters.get(parameter).getValue();
    }

    /**
     * Returns a set of the parameter names
     *
     * @return a set containing the names of all of the parameters
     */
    public Set<String> parameterSet() {
        return parameters.keySet();
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
     * @param commandInput the inputted command with arguments determining which rating system to
     *                     return
     * @return the rating system needed for the given command
     */
    public RatingSystem loadRatingSystem(CommandInput commandInput) {
        boolean cleanFlag = commandInput.getOption("clean");
        boolean weekFlag = commandInput.getOption("week");

        String league = (String) parameters.get("LEAGUE").getValue();
        int year = (int) parameters.get("YEAR").getValue();
        int week = weekFlag ? (int) parameters.get("WEEK").getValue() : -1;
        if (cleanFlag) {
            if (!ratingSystems.containsKey(league)) {
                ratingSystems.put(league, new HashMap<>());
            }
            if (!ratingSystems.get(league).containsKey(year)) {
                ratingSystems.get(league).put(year, new HashMap<>());
            }
            addWeek(weekFlag, league, year, week);
        } else {
            if (ratingSystems.containsKey(league)) {
                if (ratingSystems.get(league).containsKey(year)) {
                    if (!ratingSystems.get(league).get(year).containsKey(week)) {
                        addWeek(weekFlag, league, year, week);
                    }
                } else {
                    ratingSystems.get(league).put(year, new HashMap<>());
                    addWeek(weekFlag, league, year, week);
                }
            } else{
                ratingSystems.put(league, new HashMap<>());
                ratingSystems.get(league).put(year, new HashMap<>());
                addWeek(weekFlag, league, year, week);
            }
        }
        return ratingSystems.get(league).get(year).get(week);
    }

    /**
     * Returns a rating system based on the input of a specified command and a specified week
     *
     * Uses rating systems already created in the past to improve performance for multiple commands
     * run on the same parameters
     *
     * @param commandInput the inputted command with arguments determining which rating system to
     *                     return
     * @param week the week of the rating system to return
     * @return the rating system needed for the given command
     */
    public RatingSystem loadRatingSystem(CommandInput commandInput, int week) {
        boolean cleanFlag = commandInput.getOption("clean");

        String league = (String) parameters.get("LEAGUE").getValue();
        int year = (int) parameters.get("YEAR").getValue();
        if (cleanFlag) {
            if (!ratingSystems.containsKey(league)) {
                ratingSystems.put(league, new HashMap<>());
            }
            if (!ratingSystems.get(league).containsKey(year)) {
                ratingSystems.get(league).put(year, new HashMap<>());
            }
            addWeek(true, league, year, week);
        } else {
            if (ratingSystems.containsKey(league)) {
                if (ratingSystems.get(league).containsKey(year)) {
                    if (!ratingSystems.get(league).get(year).containsKey(week)) {
                        addWeek(true, league, year, week);
                    }
                } else {
                    ratingSystems.get(league).put(year, new HashMap<>());
                    addWeek(true, league, year, week);
                }
            } else{
                ratingSystems.put(league, new HashMap<>());
                ratingSystems.get(league).put(year, new HashMap<>());
                addWeek(true, league, year, week);
            }
        }
        return ratingSystems.get(league).get(year).get(week);
    }

    /**
     * Adds a week and a new rating system to the rating systems
     *
     * @param weekFlag whether or not the week should be passed to the rating system
     * @param league the league for the rating system
     * @param year the year for the rating system
     * @param week the week for the rating system
     */
    private void addWeek(boolean weekFlag, String league, int year, int week) {
        if (weekFlag) {
            ratingSystems.get(league).get(year).put(week, loadNewRatingSystem(interpreters.get(league), year, week));
        } else {
            ratingSystems.get(league).get(year).put(week, loadNewRatingSystem(interpreters.get(league), year));
        }
    }

    /**
     * Loads a new rating system with the given interpreter and year
     *
     * @param interpreter the interpreter with which to create the rating system
     * @param year the year for which to create the rating system
     * @return a new rating system
     */
    public abstract RatingSystem loadNewRatingSystem(Interpreter interpreter, int year);

    /**
     * Loads a new rating system with the given interpreter, year, and week
     *
     * @param interpreter the interpreter with which to create the rating system
     * @param year the year for which to create the rating system
     * @param week the week for which to create the rating system
     * @return a new rating system
     */
    public abstract RatingSystem loadNewRatingSystem(Interpreter interpreter, int year, int week);

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
