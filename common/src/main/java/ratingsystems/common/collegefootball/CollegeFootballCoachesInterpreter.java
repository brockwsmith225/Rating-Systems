package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Team;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;

public class CollegeFootballCoachesInterpreter extends CollegeFootballInterpreter {
    @Override
    public HashMap<String, Team> parseData(int year) throws FileNotFoundException {
        setup();
        Scanner data = getData(year);
        LocalDate startDate = getStartDate(year);

        while (data.hasNext()) {
            CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
            addTeam(entry.coach, entry.conference, entry.coach, year);
            teams.get(entry.coach).addGame(new Game(entry.coach, entry.opponentCoach, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
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
                addTeam(entry.coach, entry.conference, entry.coach, year);
                teams.get(entry.coach).addGame(new Game(entry.coach, entry.opponentCoach, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
            }
        }

        addDefensiveStatistics();

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int[] years, boolean cumulative) throws FileNotFoundException {
        setup();

        for (int year : years) {
            Scanner data = getData(year);
            LocalDate startDate = getStartDate(year);

            while (data.hasNext()) {
                CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
                addTeam(entry.coach, entry.conference, entry.coach, year);
                teams.get(entry.coach).addGame(new Game(entry.coach, entry.opponentCoach, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
            }
        }

        addDefensiveStatistics();

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int[] years, int week, boolean cumulative) throws FileNotFoundException {
        setup();

        for (int year : years) {
            Scanner data = getData(year);
            LocalDate startDate = getStartDate(year);
            if (year == years[years.length - 1]) {
                while (data.hasNext()) {
                    CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
                    if (entry.week <= week) {
                        addTeam(entry.coach, entry.conference, entry.coach, year);
                        teams.get(entry.coach).addGame(new Game(entry.coach, entry.opponentCoach, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                    }
                }
            } else {
                while (data.hasNext()) {
                    CollegeFootballEntry entry = new CollegeFootballEntry(data.nextLine(), startDate);
                    addTeam(entry.coach, entry.conference, entry.coach, year);
                    teams.get(entry.coach).addGame(new Game(entry.coach, entry.opponentCoach, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                }
            }
        }

        addDefensiveStatistics();

        return teams;
    }
}
