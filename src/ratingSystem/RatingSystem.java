package ratingSystem;

import interpreter.Interpreter;
import interpreter.datatypes.Entity;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class RatingSystem {
    private HashMap<String, Entity> entities;
    private ArrayList<Entity> rankedEntities;
    private ArrayList<Entity> rankedGroups;
    private Interpreter interpreter;

    /**
     * Creates a new instance of a Rating System object with no data
     */
    public RatingSystem() {
        entities = new HashMap<>();
        rankedEntities = new ArrayList<>();
        rankedGroups = new ArrayList<>();
    }
}
