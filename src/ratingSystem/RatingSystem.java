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
}
