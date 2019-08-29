package ratingsystems.common.collegefootball;

import java.time.LocalDate;
import java.util.HashMap;

public class CollegeFootballEntry {
    public static String[] statisticNames;

    public LocalDate date;
    public String team, opponent, conference, location, result;
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
        this.location = entry[3];
        this.opponent = entry[4];
        this.result = entry[5];
        this.teamScore = Integer.parseInt(entry[6]);
        this.opponentScore = Integer.parseInt(entry[7]);
        this.weightedScoreDifference = 10 + Math.abs(this.teamScore - this.opponentScore);
        for (int i = 8; i < entry.length; i++) {
            statistics.put(statisticNames[i], Double.parseDouble(entry[i]));
        }
    }

    public static void setStatisticNames(String header) {
        String[] statisticsNames = CollegeFootballInterpreter.split(header, ",");
    }

    private static int getWeek(LocalDate date, LocalDate startDate) {
        int week = (int)((365 * (date.getYear() - startDate.getYear()) + date.getDayOfYear() - startDate.getDayOfYear() + 7) / 7.0);
        if ((date.getYear() == 2013 || date.getYear() == 2014) && week > 17) {
            week = 17;
        } else if (week > 16) {
            week = 16;
        }
        return week;
    }
}
