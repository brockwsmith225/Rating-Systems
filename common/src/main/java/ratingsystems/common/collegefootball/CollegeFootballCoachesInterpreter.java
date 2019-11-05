package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CollegeFootballCoachesInterpreter extends CollegeFootballInterpreter {
    @Override
    public Map<String, Team> parseData(int year) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            if (!teams.containsKey(entry.coach)) {
                teams.put(entry.coach, new Team(entry.coach, entry.conference, entry.coach, year));
            }
            teams.get(entry.coach).addGame(new Game(entry.coach, entry.opponentCoach, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
        }

        addDefensiveStatistics(teams);

        return teams;
    }

    @Override
    public Map<String, Team> parseData(int year, int week) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            if (entry.week <= week) {
                if (!teams.containsKey(entry.coach)) {
                    teams.put(entry.coach, new Team(entry.coach, entry.conference, entry.coach, year));
                }
                teams.get(entry.coach).addGame(new Game(entry.coach, entry.opponentCoach, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
            }
        }

        addDefensiveStatistics(teams);

        return teams;
    }
}
