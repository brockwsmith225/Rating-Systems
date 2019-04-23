package cfb;

import interpreter.Interpreter;
import interpreter.datatypes.DataPoint;
import interpreter.datatypes.Entity;
import interpreter.datatypes.Time;

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

        while (data.hasNext()) {
            String[] line = split(data.nextLine(), ",");

            String[] d = line[0].split("-");
            Time date = new Time(Integer.parseInt(d[2]), Integer.parseInt(d[1]), Integer.parseInt(d[0]));
            String team = line[1];
            String conference = line[2];
            String location = line[3];
            String opponent = line[4];
            String result = line[5];
            int teamScore = Integer.parseInt(line[6]);
            int opponentScore = Integer.parseInt(line[7]);
            int scoreDifference = teamScore - opponentScore;
            int weightedScoreDifference = (scoreDifference / Math.abs(scoreDifference)) * (10 + Math.abs(scoreDifference));

            if (addedEntites.add(team)) {
                entities.put(team, new Entity(team));
            }
            if (addedEntites.add(opponent)) {
                entities.put(opponent, new Entity(opponent));
            }

            entities.get(team).setGroup(conference);
            if (addedGroups.add(conference)) {
                groups.add(conference);
            }

            entities.get(team).addDataPoint(new DataPoint(opponent, teamScore, opponentScore, weightedScoreDifference, date));
        }

        return entities;
    }

    @Override
    public HashMap<String, Entity> parseData(String filePath, Integer week) throws FileNotFoundException {
        Scanner data = new Scanner(new File(filePath));
        entities = new HashMap<>();
        addedEntites = new HashSet<>();

        Time startDate = getStartDate(data);

        data = new Scanner(new File(filePath));
        while (data.hasNext()) {
            String[] line = split(data.nextLine(), ",");
            String[] d = line[0].split("-");
            Time date = new Time(Integer.parseInt(d[2]), Integer.parseInt(d[1]), Integer.parseInt(d[0]));
            String team = line[1];
            String conference = line[2];
            String location = line[3];
            String opponent = line[4];
            String result = line[5];
            int teamScore = Integer.parseInt(line[6]);
            int opponentScore = Integer.parseInt(line[7]);
            int scoreDifference = Math.abs(teamScore - opponentScore);
            int weightedScoreDifference = (scoreDifference / Math.abs(scoreDifference)) * (10 + Math.abs(scoreDifference));
            int gameWeek = ((int)date.daysSince(startDate) + 7) / 7;
            if ((startDate.getYear() == 2013 || startDate.getYear() == 2014) && gameWeek > 17) {
                gameWeek = 17;
            } else if (week > 16) {
                gameWeek = 16;
            }
        }

        return entities;
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

    private Time getStartDate(Scanner data) {
        String[] line = split(data.nextLine(), ",");
        int year = Integer.parseInt(line[0].split("-")[0]);
        Time startDate = new Time(1, 9, year);
        while (startDate.dayOfTheWeek() != 0) {
            startDate.incrementByDays(-1);
        }
        return startDate;
    }
}
