package ratingSystem;

import interpreter.Interpreter;
import interpreter.datatypes.Entity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class RatingSystem {
    private HashMap<String, Entity> entities;
    private ArrayList<Entity> rankedEntities;
    private ArrayList<Entity> rankedGroups;
    private Interpreter interpreter;

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
     * @param filePath the file path of the data file
     * @throws FileNotFoundException if the file specified by the file path is not found
     */
    public RatingSystem(Interpreter interpreter, String filePath) throws FileNotFoundException {
        entities = interpreter.parseData(filePath);
        rankedEntities = new ArrayList<>();
        rankedGroups = new ArrayList<>();
        this.interpreter = interpreter;
    }
}
