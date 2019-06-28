package ratingsystems.common.collegefootball;

import ratingsystems.common.collegefootball.CollegeFootballInterpreter;
import ratingsystems.common.interpreter.Time;

public class Entry {
    public Time date;
    public String team, opponent, conference, location, result;
    public int teamScore, opponentScore, scoreDifference, weightedScoreDifference, week;

    /**
     * Parses the inputted line for the information needed by the College Football interpreter
     *
     * @param line the line to be parsed
     */
    public Entry(String line) {
        String[] entry = CollegeFootballInterpreter.split(line, ",");

        String[] d = entry[0].split("-");
        this.date = new Time(Integer.parseInt(d[2]), Integer.parseInt(d[1]), Integer.parseInt(d[0]));
        this.team = entry[1];
        this.conference = entry[2];
        this.location = entry[3];
        this.opponent = entry[4];
        this.result = entry[5];
        this.teamScore = Integer.parseInt(entry[6]);
        this.opponentScore = Integer.parseInt(entry[7]);
        this.scoreDifference = Math.abs(teamScore - opponentScore);
        this.weightedScoreDifference = ((teamScore - opponentScore) / scoreDifference) * (10 + scoreDifference);
        this.week = 0;
    }

    /**
     * Parses the inputted line for the information needed by the College Football interpreter
     *
     * @param line the line to be parsed
     * @param startDate the starting date of the year to be used to calculate the week of the game
     */
    public Entry(String line, Time startDate) {
        String[] entry = CollegeFootballInterpreter.split(line, ",");

        String[] d = entry[0].split("-");
        this.date = new Time(Integer.parseInt(d[2]), Integer.parseInt(d[1]), Integer.parseInt(d[0]));
        this.team = entry[1];
        this.conference = entry[2];
        this.location = entry[3];
        this.opponent = entry[4];
        this.result = entry[5];
        this.teamScore = Integer.parseInt(entry[6]);
        this.opponentScore = Integer.parseInt(entry[7]);
        this.scoreDifference = Math.abs(teamScore - opponentScore);
        this.weightedScoreDifference = ((teamScore - opponentScore) / scoreDifference) * (10 + scoreDifference);
        this.week = ((int)date.daysSince(startDate) + 7) / 7;
    }
}
