package ratingsystems.common.collegefootball;

import java.time.LocalDate;

public class CollegeFootballEntry {
    public LocalDate date;
    public String team, opponent, conference, location, result;
    public int teamScore, opponentScore, scoreDifference, weightedScoreDifference, week;

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
        this.scoreDifference = Math.abs(teamScore - opponentScore);
        this.weightedScoreDifference = ((teamScore - opponentScore) / scoreDifference) * (10 + scoreDifference);
    }

    private static int getWeek(LocalDate date, LocalDate startDate) {
        int week = (int)((date.getDayOfYear() - startDate.getDayOfYear() + 7) / 7.0);
        if ((date.getYear() == 2013 || date.getYear() == 2014) && week > 17) {
            week = 17;
        } else if (week > 16) {
            week = 16;
        }
        return week;
    }
}
