package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CollegeFootballConferencesInterpreter extends CollegeFootballInterpreter {
    @Override
    public Map<String, Team> parseData(int year) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            if (!teams.containsKey(entry.conference)) {
                teams.put(entry.conference, new Team(entry.conference, entry.conference, entry.coach, year));
            }
            if (!entry.conference.equals(entry.opponentConference)) {
                teams.get(entry.conference).addGame(new Game(entry.conference, entry.opponentConference, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
            }
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
                if (!teams.containsKey(entry.conference)) {
                    teams.put(entry.conference, new Team(entry.conference, entry.conference, entry.coach, year));
                }
                if (!entry.conference.equals(entry.opponentConference)) {
                    teams.get(entry.conference).addGame(new Game(entry.conference, entry.opponentConference, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                }
            }
        }

        addDefensiveStatistics(teams);

        return teams;
    }
}
