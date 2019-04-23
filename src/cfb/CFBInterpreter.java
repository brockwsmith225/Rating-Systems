package cfb;

import interpreter.Interpreter;
import interpreter.LimitingFunction;
import interpreter.datatypes.Entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class CFBInterpreter extends Interpreter<Integer> {

    @Override
    public HashMap<String, Entity> parseData(String filePath) throws FileNotFoundException {
        Scanner data = new Scanner(new File(filePath));
        entities = new HashMap<>();
        addedEntites = new HashSet<>();

        double[] avgStats = getAvgStats(data);

        data = new Scanner(new File(filePath));
        while (data.hasNext()) {

        }

        return entities;
    }

    @Override
    public HashMap<String, Entity> parseData(String filePath, Integer week) throws FileNotFoundException {
        return null;
    }

    @Override
    public HashMap<String, Entity> parseData(String[] filePaths) throws FileNotFoundException {
        return null;
    }

    //========== CFB only methods ==========

    private double[] getAvgStats(Scanner data) {
        double[] avgStats = new double[13];
        int count = 0;
        while (data.hasNext()) {
            String[] line = split(data.nextLine(), ",");
            count++;
            avgStats[0] += Double.parseDouble(line[6]);
            for (int i = 1; i < avgStats.length; i++) {
                avgStats[i] += Double.parseDouble(line[i+7]);
            }
        }
        for (int i = 0; i < avgStats.length; i++) {
            avgStats[i] /= count;
        }
        return avgStats;
    }
}
