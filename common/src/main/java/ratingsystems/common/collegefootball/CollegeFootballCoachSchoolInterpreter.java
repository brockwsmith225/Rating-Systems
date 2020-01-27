package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CollegeFootballCoachSchoolInterpreter extends CollegeFootballInterpreter {
    @Override
    public Map<String, Team> parseData(int year) throws FileNotFoundException {
        Map<String, Team> teams = new HashMap<>();

        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            if (entry.coach.length() > 0) {
                if (!teams.containsKey(entry.coach + "\t" + entry.team)) {
                    teams.put(entry.coach + "\t" + entry.team, new Team(entry.coach + "\t" + entry.team, entry.conference, entry.coach, year));
                }
                if (entry.opponentCoach.length() > 0) {
                    teams.get(entry.coach + "\t" + entry.team).addGame(new Game(entry.coach + "\t" + entry.team, entry.opponentCoach + "\t" + entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                }
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
            if (entry.week <= week && entry.coach.length() > 0) {
                if (!teams.containsKey(entry.coach + "\t" + entry.team)) {
                    teams.put(entry.coach + "\t" + entry.team, new Team(entry.coach + "\t" + entry.team, entry.conference, entry.coach, year));
                }
                if (entry.opponentCoach.length() > 0) {
                    teams.get(entry.coach + "\t" + entry.team).addGame(new Game(entry.coach + "\t" + entry.team, entry.opponentCoach + "\t" + entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                }
            }
        }

        addDefensiveStatistics(teams);

        return teams;
    }
}
