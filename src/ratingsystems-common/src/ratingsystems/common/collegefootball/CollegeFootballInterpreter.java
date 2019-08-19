package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.interpreter.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class CollegeFootballInterpreter extends Interpreter {

    @Override
    public HashMap<String, Team> parseData(int year) throws FileNotFoundException {
        Scanner data = getData(year);

        teams = new HashMap<>();
        groups = new ArrayList<>();
        addedTeams = new HashSet<>();
        addedGroups = new HashSet<>();

        LocalDate startDate = getStartDate(data);

        while (data.hasNext()) {
            Entry entry = new Entry(data.nextLine(), startDate);

            if ((startDate.getYear() == 2013 || startDate.getYear() == 2014) && entry.week > 17) {
                entry.week = 17;
            } else if (entry.week > 16) {
                entry.week = 16;
            }

            addTeam(entry.team, entry.conference);

            teams.get(entry.team).addGame(new Game(entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date));
        }

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int year, int week) throws FileNotFoundException {
        Scanner data = getData(year);

        teams = new HashMap<>();
        groups = new ArrayList<>();
        addedTeams = new HashSet<>();
        addedGroups = new HashSet<>();

        LocalDate startDate = getStartDate(data);

        while (data.hasNext()) {
            Entry entry = new Entry(data.nextLine(), startDate);

            if ((startDate.getYear() == 2013 || startDate.getYear() == 2014) && entry.week > 17) {
                entry.week = 17;
            } else if (entry.week > 16) {
                entry.week = 16;
            }

            if (entry.week <= week) {
                addTeam(entry.team, entry.conference);

                teams.get(entry.team).addGame(new Game(entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date));
            }
        }

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int[] years) throws FileNotFoundException {
        teams = new HashMap<>();
        groups = new ArrayList<>();
        addedTeams = new HashSet<>();
        addedGroups = new HashSet<>();

        for (int year : years) {
            Scanner data = getData(year);

            while (data.hasNext()) {
                Entry entry = new Entry(data.nextLine());

                addTeam(entry.team, entry.conference);

                teams.get(entry.team).addGame(new Game(entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date));
            }
        }
        return teams;
    }

    @Override
    public void addTeam(String team, String conference) {
        if (addedTeams.add(team)) {
            teams.put(team, new Team(team));
        }

        teams.get(team).setGroup(conference);
        if (addedGroups.add(conference)) {
            groups.add(conference);
        }
    }

    @Override
    public boolean hasData(int year)  {
        return new File("ratingsystems/src/data/cfb-" + year + ".csv").exists();
    }

    @Override
    public Scanner getData(int year) throws FileNotFoundException {
        Scanner data = new Scanner(new File("ratingsystems/src/data/cfb-" + year + ".csv"));
        data.nextLine();
        return data;
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

    private LocalDate getStartDate(Scanner data) {
        String[] line = split(data.nextLine(), ",");
        int year = Integer.parseInt(line[0].split("-")[0]);
        LocalDate startDate = LocalDate.of(year, 9, 1);
        while (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            startDate = startDate.minusDays(1);
        }
        return startDate;
    }
}
