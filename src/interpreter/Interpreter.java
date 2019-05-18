package interpreter;

import interpreter.datatypes.Entity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

public abstract class Interpreter<T> {

    protected HashMap<String, Entity> entities;
    protected HashSet<String> addedEntites;
    protected ArrayList<String> groups;
    protected HashSet<String> addedGroups;

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param year the year of the data
     * @return a collection of the entities found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public HashMap<String, Entity> parseData(int year) throws FileNotFoundException;

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param year the year of the data
     * @param limitingParameter the parameter which tells the interpreter which data from the data file to include
     * @return a collection of the entities found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public HashMap<String, Entity> parseData(int year, T limitingParameter) throws FileNotFoundException;

    /**
     * Interprets the data found in the data files specified by the file paths
     *
     * @param years the years of the data
     * @return a collection of the entities found in the data file
     * @throws FileNotFoundException if any of the data files specified by the file paths is not found
     */
    abstract public HashMap<String, Entity> parseData(int[] years) throws FileNotFoundException;

    /**
     * Returns the groups of the entities
     *
     * @return the groups
     */
    public ArrayList<String> groups() {
        return new ArrayList<>(groups);
    }

    /**
     * Checks to see if the data file specified by the file path exists
     *
     * @param year the year of the data
     * @return true if the file exists, false otherwise
     */
    abstract public boolean hasData(int year);

    /**
     * Splits the inputted string by the inputted delimiter. Ignores
     * portions of the inputted string that are within quotes
     *
     * @param input the input to be split
     * @return the split input
     */
    protected static String[] split(String input, String delimiter) {
        String[] res = input.split(delimiter + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < res.length; i++) {
            res[i] = res[i].replace("\"", "");
        }
        return res;
    }
}
