package ratingSystem;

import interpreter.Interpreter;
import interpreter.LimitingFunction;
import interpreter.datatypes.Entity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class RatingSystem {
    protected HashMap<String, Entity> entities;
    protected ArrayList<Entity> rankedEntities;
    protected ArrayList<Entity> rankedGroups;
    protected Interpreter interpreter;

    /**
     * Creates a new instance of a Rating System with no data
     */
    public RatingSystem() {
        entities = new HashMap<>();
        rankedEntities = new ArrayList<>();
        rankedGroups = new ArrayList<>();
    }

    /**
     * Creates a new instance of a Rating System with the data found in the file specified by the file path
     *
     * @param interpreter the interpreter to be used to read in the data from the data file
     * @param year the year of the data
     * @throws FileNotFoundException if the file specified by the file path is not found
     */
    public RatingSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        entities = interpreter.parseData(year);
        rankedEntities = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }

    /**
     * Creates a new instance of a Rating System with the data found in the file specified by the file path
     *
     * @param interpreter the interpreter to be used to read in the data from the data file
     * @param year the data of the data
     * @param limitingFunction the function which limits which data from the data file is included
     * @throws FileNotFoundException if the file specified by the file path is not found
     */
    public RatingSystem(Interpreter interpreter, int year, LimitingFunction limitingFunction) throws FileNotFoundException {
        entities = interpreter.parseData(year, limitingFunction);
        rankedEntities = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }

    /**
     * Setups the method for rating the entities as necessary
     */
    abstract public void setup();

    /**
     * Ranks the entities
     */
    abstract public void rankEntities();

    /**
     * Ranks the groups of entities
     */
    abstract public void rankGroups();

    /**
     * Prints the entities in ranked order along with their ratings
     */
    public void printEntityRankings() {
        System.out.println(rankedEntities.size());
        int rank = 1;
        for (int i = 0; i < rankedEntities.size(); i++) {
            if (i > 0 && rankedEntities.get(i).getRating() != rankedEntities.get(i).getRating()) {
                rank = i + 1;
            }
            System.out.println(rank + ". " + rankedEntities.get(i) + " " + rankedEntities.get(i).getRating());
        }
    }
}
