package ratingsystems.common.collegebasketball;

import ratingsystems.common.interpreter.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CollegeBasketballInterpreter extends Interpreter {

    @Override
    public Map<String, Team> parseData(int year) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeBasketballEntry entry = new CollegeBasketballEntry(data.nextLine(), startDate);
            if (!teams.containsKey(entry.team)) {
                teams.put(entry.team, new Team(entry.team, entry.conference, entry.coach, year));
            }
            teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date));
        }

        return teams;
    }

    @Override
    public Map<String, Team> parseData(int year, int week) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeBasketballEntry entry = new CollegeBasketballEntry(data.nextLine(), startDate);
            if (entry.week <= week) {
                if (!teams.containsKey(entry.team)) {
                    teams.put(entry.team, new Team(entry.team, entry.conference, entry.coach, year));
                }
                teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date));
            }
        }

        return teams;
    }

    @Override
    public boolean hasData(int year)  {
        return new File("data/cbb-" + year + ".csv").exists();
    }

    @Override
    public Scanner getData(int year) throws FileNotFoundException {
        Scanner data = new Scanner(new File("data/cbb-" + year + ".csv"));
        data.nextLine();
        return data;
    }

    @Override
    public boolean hasBracket(int year) {
        return new File("data/cbb-bracket-" + year + ".txt").exists();
    }

    @Override
    public Bracket parseBracket(int year) throws FileNotFoundException {
        return new BracketInterpreter().getBracket("data/cfb-bracket-" + year + ".txt");
    }

    @Override
    public void fetchData(int year) throws IOException {
        new CollegeBasketballScraper().fetch(year);
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
        LocalDate startDate = LocalDate.of(year-1, 11, 6);
        while (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            startDate = startDate.minusDays(1);
        }
        return startDate;
    }

}
