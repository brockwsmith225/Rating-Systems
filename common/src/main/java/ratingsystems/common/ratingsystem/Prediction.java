package ratingsystems.common.ratingsystem;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Location;

public class Prediction {
    private String team1, team2;
    private double odds, line, overUnder, team1Score, team2Score;
    private Location location;

    public Prediction(String team1, String team2, double odds) {
        this.team1 = team1;
        this.team2 = team2;
        this.odds = odds;
        this.line = 0;
        this.overUnder = -1;
        this.team1Score = -1;
        this.team2Score = -1;
        this.location = null;
    }

    public Prediction(String team1, String team2, double odds, Location location) {
        this(team1, team2, odds);
        this.location = location;
    }

    public Prediction(String team1, String team2, double odds, double line) {
        this.team1 = team1;
        this.team2 = team2;
        this.odds = odds;
        this.line = (Math.round(line * 2) / 2.0);
        this.overUnder = -1;
        this.team1Score = -1;
        this.team2Score = -1;
        this.location = null;
    }

    public Prediction(String team1, String team2, double odds, double line, Location location) {
        this(team1, team2, odds, line);
        this.location = location;
    }

    public Prediction(String team1, String team2, double odds, double team1Score, double team2Score) {
        this.team1 = team1;
        this.team2 = team2;
        this.odds = odds;
        this.team1Score = (Math.round(team1Score * 2) / 2.0);
        this.team2Score = (Math.round(team2Score * 2) / 2.0);
        this.line = this.team2Score - this.team1Score;
        this.overUnder = this.team1Score + this.team2Score;
        this.location = null;
    }

    public Prediction(String team1, String team2, double odds, double team1Score, double team2Score, Location location) {
        this(team1, team2, odds, team1Score, team2Score);
        this.location = location;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public double getOdds() {
        return odds;
    }

    public double getLine() {
        return line;
    }

    public double getOverUnder() {
        return overUnder;
    }

    public double getTeam1Score() {
        return team1Score;
    }

    public double getTeam2Score() {
        return team2Score;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (team1Score >= 0 && team2Score >= 0) {
            //Team 1
            if (location != null && location != Location.NEUTRAL) {
                if (location == Location.HOME) {
                    output.append("H  ");
                } else {
                    output.append("A  ");
                }
            }
            output.append(Terminal.leftJustify(team1, 20));
            output.append(Terminal.rightJustify(formatScore(team1Score), 10));
            output.append(Terminal.rightJustify(Terminal.round(odds * 100, 2), 10));
            output.append("%");
            output.append(Terminal.rightJustify(formatLine(line), 10));
            output.append("\n");

            //Team 2
            if (location != null && location != Location.NEUTRAL) {
                if (location == Location.HOME) {
                    output.append("A  ");
                } else {
                    output.append("H  ");
                }
            }
            output.append(Terminal.leftJustify(team2, 20));
            output.append(Terminal.rightJustify(formatScore(team2Score), 10));
            output.append(Terminal.rightJustify(Terminal.round((1-odds) * 100, 2), 10));
            output.append("%");
        } else {
            if (location != null && location != Location.NEUTRAL) {
                if (location == Location.HOME) {
                    output.append("H  ");
                } else {
                    output.append("A  ");
                }
            }
            output.append(Terminal.leftJustify(team1, 20));
            output.append(Terminal.rightJustify(Terminal.round(odds * 100, 2), 10));
            output.append("%");
            output.append(Terminal.rightJustify(formatLine(line), 10));
        }
        if (overUnder >= 0) {
            output.append("\nO/U ");
            output.append(formatScore(overUnder));
        }
        return output.toString();
    }

    private String formatScore(double score) {
        String formattedScore = Double.toString((Math.round(score * 2) / 2.0));
        if (!formattedScore.contains(".")) formattedScore += ".0";
        return formattedScore;
    }

    private String formatLine(double line) {
        return (line >= 0 ? "+" : "") + formatScore(line);
    }

}
