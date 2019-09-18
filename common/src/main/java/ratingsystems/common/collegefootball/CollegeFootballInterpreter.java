package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;

public class CollegeFootballInterpreter extends Interpreter {

    @Override
    public HashMap<String, Team> parseData(int year) throws FileNotFoundException {
        setup();
        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            addTeam(entry.team, entry.conference);
            teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
        }

        addDefensiveStatistics();

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int year, int week) throws FileNotFoundException {
        setup();
        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            if (entry.week <= week) {
                addTeam(entry.team, entry.conference);
                //entry.weightedScoreDifference = (int)(entry.weightedScoreDifference * Math.pow(0.975, week - entry.week));
                teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
            }
        }

        addDefensiveStatistics();

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int[] years) throws FileNotFoundException {
        setup();

        for (int year : years) {
            Scanner data = getData(year);
            LocalDate startDate = getStartDate(year);

            while (data.hasNext()) {
                CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
                addTeam(entry.team, entry.conference);
                teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
            }
        }

        addDefensiveStatistics();

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int[] years, int week) throws FileNotFoundException {
        setup();

        for (int year : years) {
            Scanner data = getData(year);
            LocalDate startDate = getStartDate(year);
            if (year == years[years.length - 1]) {
                while (data.hasNext()) {
                    CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
                    if (entry.week <= week) {
                        addTeam(entry.team, entry.conference);
                        teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                    }
                }
            } else {
                while (data.hasNext()) {
                    CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
                    addTeam(entry.team, entry.conference);
                    teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                }
            }
        }

        addDefensiveStatistics();

        return teams;
    }

    @Override
    public void addTeam(String team, String conference) {
        if (addedTeams.add(team)) {
            teams.put(team, new Team(team));
        }

        teams.get(team).setConference(conference);
        if (addedGroups.add(conference)) {
            groups.add(conference);
        }
    }

    @Override
    public boolean hasData(int year)  {
        return new File("data/cfb-" + year + ".csv").exists();
    }

    @Override
    public Scanner getData(int year) throws FileNotFoundException {
        Scanner data = new Scanner(new File("data/cfb-" + year + ".csv"));
        CollegeFootballEntry.setStatisticNames(data.nextLine());
        return data;
    }

    @Override
    public void fetchData(int year) throws IOException {
        new CollegeFootballScraper().fetch(year);
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

    private LocalDate getStartDate(int year) {
        LocalDate startDate = LocalDate.of(year, 9, 1);
        while (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            startDate = startDate.minusDays(1);
        }
        return startDate;
    }
}
