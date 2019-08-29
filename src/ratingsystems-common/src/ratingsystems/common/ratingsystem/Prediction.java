package ratingsystems.common.ratingsystem;

import ratingsystems.common.cli.Terminal;

public class Prediction {
    private String team1, team2;
    private double odds, line, overUnder, team1Score, team2Score;

    public Prediction(String team1, String team2, double odds) {
        this.team1 = team1;
        this.team2 = team2;
        this.odds = odds;
        this.line = -1;
        this.overUnder = -1;
        this.team1Score = -1;
        this.team2Score = -1;
    }

    public Prediction(String team1, String team2, double odds, double line) {
        this.team1 = team1;
        this.team2 = team2;
        this.odds = odds;
        this.line = (Math.round(line * 2) / 2.0);
        this.overUnder = -1;
        this.team1Score = -1;
        this.team2Score = -1;
    }

    public Prediction(String team1, String team2, double odds, double team1Score, double team2Score) {
        this.team1 = team1;
        this.team2 = team2;
        this.odds = odds;
        this.team1Score = (Math.round(team1Score * 2) / 2.0);
        this.team2Score = (Math.round(team2Score * 2) / 2.0);
        this.line = this.team2Score - this.team1Score;
        this.overUnder = this.team1Score + this.team2Score;
    }

    public double getOdds() {
        return odds;
    }

    public double getLine() {
        return line;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (team1Score >= 0 && team2Score >= 0) {
            //Team 1
            output.append(Terminal.leftJustify(team1, 20));
            output.append(Terminal.rightJustify(formatScore(team1Score), 10));
            output.append(Terminal.rightJustify(Terminal.round(odds * 100, 2), 10));
            output.append("%");
            output.append(Terminal.rightJustify(formatLine(line), 10));
            output.append("\n");

            //Team 2
            output.append(Terminal.leftJustify(team2, 20));
            output.append(Terminal.rightJustify(formatScore(team2Score), 10));
            output.append(Terminal.rightJustify(Terminal.round((1-odds) * 100, 2), 10));
            output.append("%");
        } else {
            output.append(Terminal.leftJustify(team1, 20));
            output.append(Terminal.rightJustify(Terminal.round(odds * 100, 2), 10));
            output.append("%");
            if (line >= 0) {
                output.append(Terminal.rightJustify(formatLine(line), 10));
            }
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
