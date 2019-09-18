package ratingsystems.common.collegebasketball;

import java.time.LocalDate;

public class CollegeBasketballEntry {
    public LocalDate date;
    public String team, opponent, conference, location, result;
    public int teamScore, opponentScore, weightedScoreDifference, week;

    /**
     * Parses the inputted line for the information needed by the College Basketball interpreter
     *
     * @param line the line to be parsed
     * @param startDate the starting date of the year to be used to calculate the week of the game
     */
    public CollegeBasketballEntry(String line, LocalDate startDate) {
        String[] entry = CollegeBasketballInterpreter.split(line, ",");

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
    }

    private static int getWeek(LocalDate date, LocalDate startDate) {
        return (int)((365 * (date.getYear() - startDate.getYear()) + date.getDayOfYear() - startDate.getDayOfYear() + 7) / 7.0);
    }
}
