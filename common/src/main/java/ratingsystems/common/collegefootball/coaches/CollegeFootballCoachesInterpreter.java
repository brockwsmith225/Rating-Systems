package ratingsystems.common.collegefootball.coaches;

import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
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
            CollegeFootballCoachesEntry entry = new CollegeFootballCoachesEntry(data.nextLine(), startDate);
            addTeam(entry.team, entry.conference);
            teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
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
            CollegeFootballCoachesEntry entry = new CollegeFootballCoachesEntry(data.nextLine(), startDate);
            if (entry.week <= week) {
                addTeam(entry.team, entry.conference);
                //entry.weightedScoreDifference = (int)(entry.weightedScoreDifference * Math.pow(0.975, week - entry.week));
                teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
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
                CollegeFootballCoachesEntry entry = new CollegeFootballCoachesEntry(data.nextLine(), startDate);
                addTeam(entry.team, entry.conference);
                teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
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
                    CollegeFootballCoachesEntry entry = new CollegeFootballCoachesEntry(data.nextLine(), startDate);
                    if (entry.week <= week) {
                        addTeam(entry.team, entry.conference);
                        teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                    }
                }
            } else {
                while (data.hasNext()) {
                    CollegeFootballCoachesEntry entry = new CollegeFootballCoachesEntry(data.nextLine(), startDate);
                    addTeam(entry.team, entry.conference);
                    teams.get(entry.team).addGame(new Game(entry.team, entry.opponent, entry.location, entry.teamScore, entry.opponentScore, entry.weightedScoreDifference, entry.week, entry.date, entry.statistics));
                }
            }
        }

        addDefensiveStatistics();

        return teams;
    }
}
