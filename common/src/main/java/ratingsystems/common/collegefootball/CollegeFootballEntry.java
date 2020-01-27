package ratingsystems.common.collegefootball;

import ratingsystems.common.interpreter.Location;

import java.time.LocalDate;
import java.util.HashMap;

public class CollegeFootballEntry {
    public static String[] statisticNames;

    public LocalDate date;
    public String team, conference, coach, opponent, opponentConference, opponentCoach;
    public Location location;
    public char result;
    public int teamScore, opponentScore, weightedScoreDifference, week;
    public HashMap<String, Double> statistics;

    /**
     * Parses the inputted line for the information needed by the College Football interpreter
     *
     * @param line the line to be parsed
     * @param startDate the starting date of the year to be used to calculate the week of the game
     */
    public CollegeFootballEntry(String line, LocalDate startDate) {
        String[] entry = CollegeFootballInterpreter.split(line, ",");

        String[] d = entry[0].split("-");
        this.date = LocalDate.of(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]));
        this.week = getWeek(date, startDate);
        this.team = entry[1];
        this.conference = entry[2];
        this.coach = entry[3];
        char loc = entry[4].charAt(0);
        this.location = loc == 'H' ? Location.HOME : loc == 'A' ? Location.AWAY : Location.NEUTRAL;
        this.opponent = entry[5];
        this.opponentConference = entry[6];
        this.opponentCoach = entry[7];
        this.result = entry[8].charAt(0);
        this.teamScore = Integer.parseInt(entry[9]);
        this.opponentScore = Integer.parseInt(entry[10]);
        this.weightedScoreDifference = 10 + Math.abs(this.teamScore - this.opponentScore);
        this.statistics = new HashMap<>();
        for (int i = 11; i < entry.length; i++) {
            this.statistics.put(statisticNames[i], Double.parseDouble(entry[i]));
        }
    }

    public static void setStatisticNames(String header) {
        statisticNames = CollegeFootballInterpreter.split(header, ",");
    }

    private static int getWeek(LocalDate date, LocalDate startDate) {
        int week = (int)((365 * (date.getYear() - startDate.getYear()) + date.getDayOfYear() - startDate.getDayOfYear() + 7) / 7.0);
        if ((startDate.getYear() == 2013 || startDate.getYear() == 2014 || startDate.getYear() == 2019) && week > 17) {
            week = 17;
        } else if (week > 16) {
            week = 16;
        }
        return week;
    }
}
