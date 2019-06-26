package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.datatypes.Game;
import ratingsystems.common.interpreter.datatypes.Team;
import ratingsystems.common.interpreter.datatypes.Time;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class CollegeFootballInterpreter extends Interpreter {

    @Override
    public HashMap<String, Team> parseData(int year) throws FileNotFoundException {
        Scanner data = new Scanner(new File("ratingsystems/src/collegefootball/data/cfb-" + year + ".csv"));
        teams = new HashMap<>();
        groups = new ArrayList<>();
        addedTeams = new HashSet<>();
        addedGroups = new HashSet<>();

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

            if (addedTeams.add(team)) {
                teams.put(team, new Team(team));
            }
            if (addedTeams.add(opponent)) {
                teams.put(opponent, new Team(opponent));
            }

            teams.get(team).setGroup(conference);
            if (addedGroups.add(conference)) {
                groups.add(conference);
            }

            teams.get(team).addGame(new Game(opponent, teamScore, opponentScore, weightedScoreDifference, date));
        }

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int year, int week) throws FileNotFoundException {
        Scanner data = new Scanner(new File("ratingsystems/src/collegefootball/data/cfb-" + year + ".csv"));
        teams = new HashMap<>();
        addedTeams = new HashSet<>();

        Time startDate = getStartDate(data);

        data = new Scanner(new File("ratingsystems/src/collegefootball/data/cfb-" + year + ".csv"));
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

            if (gameWeek <= week) {
                if (addedTeams.add(team)) {
                    teams.put(team, new Team(team));
                }
                if (addedTeams.add(opponent)) {
                    teams.put(opponent, new Team(opponent));
                }

                teams.get(team).setGroup(conference);
                if (addedGroups.add(conference)) {
                    groups.add(conference);
                }

                teams.get(team).addGame(new Game(opponent, teamScore, opponentScore, weightedScoreDifference, date));
            }
        }

        return teams;
    }

    @Override
    public HashMap<String, Team> parseData(int[] years) throws FileNotFoundException {
        teams = new HashMap<>();
        addedTeams = new HashSet<>();
        for (int year : years) {
            Scanner data = new Scanner(new File("ratingsystems/src/collegefootball/data/cfb-" + year + ".csv"));

            Time startDate = getStartDate(data);

            data = new Scanner(new File("ratingsystems/src/collegefootball/data/cfb-" + year + ".csv"));
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

                if (addedTeams.add(team)) {
                    teams.put(team, new Team(team));
                }
                if (addedTeams.add(opponent)) {
                    teams.put(opponent, new Team(opponent));
                }

                teams.get(team).setGroup(conference);
                if (addedGroups.add(conference)) {
                    groups.add(conference);
                }

                teams.get(team).addGame(new Game(opponent, teamScore, opponentScore, weightedScoreDifference, date));
            }
        }
        return teams;
    }

    @Override
    public boolean hasData(int year)  {
        return new File("ratingsystems/src/collegefootball/data/cfb-" + year + ".csv").exists();
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
