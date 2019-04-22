package interpreter;

import interpreter.datatypes.Entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

public abstract class Interpreter {

    protected HashSet<String> addedEntites;
    protected ArrayList<Entity> entities;

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param filePath the path to the data file
     * @return a collection of the entities found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public HashMap<String, Entity> parseData(String filePath) throws FileNotFoundException;

    /**
     * Interprets the data found in the data file specified by the file path
     *
     * @param filePath the path to the data file
     * @param limitingFunction the function which tells the interpreter which data from the data file to include
     * @return a collection of the entities found in the data file
     * @throws FileNotFoundException if the data file specified by the file path is not found
     */
    abstract public HashMap<String, Entity> parseData(String filePath, LimitingFunction limitingFunction) throws FileNotFoundException;

    /**
     * Interprets the data found in the data files specified by the file paths
     *
     * @param filePaths the paths to the data files
     * @return a collection of the entities found in the data file
     * @throws FileNotFoundException if any of the data files specified by the file paths is not found
     */
    abstract public HashMap<String, Entity> parseData(String[] filePaths) throws FileNotFoundException;

    /**
     * Returns the groups of the entities
     *
     * @return the groups
     */
    public ArrayList<String> groups() {
        HashSet<String> addedGroups = new HashSet<>();
        ArrayList<String> groups = new ArrayList<>();
        for (Entity entity : entities) {
            if (addedGroups.add(entity.getGroup())) {
                groups.add(entity.getGroup());
            }
        }
        return groups;
    }

    /**
     * Checks to see if the data file specified by the file path exists
     *
     * @param filePath the file path of the data file
     * @return true if the file exists, false otherwise
     */
    public boolean hasData(String filePath) {
        return new File(filePath).exists();
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
}
