package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CollegeFootballInterpreter extends Interpreter {

    @Override
    public Map<String, Team> parseData(int year) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            if (!teams.containsKey(entry.team)) {
                teams.put(entry.team, new Team(entry.team, entry.conference, entry.coach, year));
            }
            teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
        }

        addDefensiveStatistics(teams);

        data = new Scanner(new File("data/cfb-teams.csv"));
        while (data.hasNext()) {
            String team = data.nextLine().replace("\n", "").replace(",", "");
            if (!teams.containsKey(team)) {
                teams.put(team, new Team(team, "", "", year));
            }
        }

        return teams;
    }

    @Override
    public Map<String, Team> parseData(int year, int week) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            if (!teams.containsKey(entry.team)) {
                teams.put(entry.team, new Team(entry.team, entry.conference, entry.coach, year));
            }
            if (entry.week <= week) {
                teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
            }
        }

        addDefensiveStatistics(teams);

        data = new Scanner(new File("data/cfb-teams.csv"));
        while (data.hasNext()) {
            String team = data.nextLine().replace("\n", "").replace(",", "");
            if (!teams.containsKey(team)) {
                teams.put(team, new Team(team, "", "", year));
            }
        }

        return teams;
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
    public boolean hasBracket(int year) {
        return new File("data/cfb-bracket-" + year + ".txt").exists();
    }

    @Override
    public Bracket parseBracket(int year) throws FileNotFoundException {
        return new BracketInterpreter().getBracket("data/cfb-bracket-" + year + ".txt");
    }

    @Override
    public void fetchData(int year) throws IOException {
        new CollegeFootballScraper().fetch(year);
    }

    @Override
    public void fetchBracket(int year) throws IOException {
        new CollegeFootballScraper().fetchBracket(year);
    }

    //========== CFB only methods ==========
    protected LocalDate getStartDate(int year) {
        LocalDate startDate = LocalDate.of(year, 9, 1);
        while (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            startDate = startDate.minusDays(1);
        }
        return startDate;
    }
}
