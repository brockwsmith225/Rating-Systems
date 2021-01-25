package ratingsystems.dps;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.sdr.SimpleDifferenceRating;

import java.io.FileNotFoundException;
import java.util.*;

public class DifferencePredictionSystem extends RatingSystem {

    private static final double RECENCY_BIAS = 0.75;
    private static final int NUM_OF_YEARS = 1;

    private double ppg, ppgStdDev;
    private Map<String, DPSTeam> teams;
    private SimpleDifferenceRating[] sdr;

    public DifferencePredictionSystem() {
        super();
    }

    public DifferencePredictionSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new DPSTeam(super.teams.get(team)));
        }
    }

    public DifferencePredictionSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new DPSTeam(super.teams.get(team)));
        }
    }

    public DifferencePredictionSystem(Interpreter interpreter, int[] years, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new DPSTeam(super.teams.get(team)));
        }
    }

    public DifferencePredictionSystem(Interpreter interpreter, int[] years, int week, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, week, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new DPSTeam(super.teams.get(team)));
        }
    }

    @Override
    public void setup() {
        int prevYears = NUM_OF_YEARS;
        try {
            if (this.interpreter.hasData(this.year) && this.week != 0) {
                sdr = new SimpleDifferenceRating[prevYears + 1];
                for (int i = 0; i < sdr.length - 1; i++) {
                    this.sdr[i] = new SimpleDifferenceRating(this.interpreter, this.year - (prevYears - i));
                    this.sdr[i].setup();
                }
                if (this.week == -1) {
                    this.sdr[sdr.length - 1] = new SimpleDifferenceRating(this.interpreter, this.year);
                } else {
                    this.sdr[sdr.length - 1] = new SimpleDifferenceRating(this.interpreter, this.year, this.week);
                }
                this.sdr[sdr.length - 1].setup();
            } else {
                sdr = new SimpleDifferenceRating[prevYears];
                for (int i = 0; i < sdr.length; i++) {
                    this.sdr[i] = new SimpleDifferenceRating(this.interpreter, this.year - (prevYears - i));
                    this.sdr[i].setup();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ppg = sdr[sdr.length - 1].getPPG();

        calculateEfficiencies();

        rankTeams();
    }

    @Override
    public void rankTeams() {
        rankedTeams = new ArrayList<>(teams.values());
        Collections.sort(rankedTeams);
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        if (!teams.keySet().contains(team1) || !teams.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        double team1OffensiveEfficiency = teams.get(team1).getOffensiveRating();
        double team1DefensiveEfficiency = teams.get(team1).getDefensiveRating();
        double team2OffensiveEfficiency = teams.get(team2).getOffensiveRating();
        double team2DefensiveEfficiency = teams.get(team2).getDefensiveRating();

        double team1Production = team1OffensiveEfficiency + team2DefensiveEfficiency;
        double team2Production = team2OffensiveEfficiency + team1DefensiveEfficiency;

        double team1Score = team1Production + ppg;
        double team2Score = team2Production + ppg;

        double team1PPGStdDev = 0.0;
        double team1OPPGStdDev = 0.0;
        double team1Count = 0.0;
        double team2PPGStdDev = teams.get(team2).getPointsPerGameStDev();
        double team2OPPGStdDev = teams.get(team2).getPointsAllowedPerGameStDev();
        double team2Count = 0.0;
        for (int i = 0; i < sdr.length; i++) {
            if (sdr[i].hasTeam(team1)) {
                double team1RecencyModifier = Math.pow(RECENCY_BIAS, (sdr.length - i - 1) * (this.teams.get(team1).getNumberOfGames() == 0 ? 1 : this.teams.get(team1).getNumberOfGames()));
                double team1GamesModifier = 1 - Math.exp(-1 * sdr[i].getTeam(team1).getNumberOfGames());
                team1PPGStdDev += sdr[i].getSDRTeam(team1).getPointsPerGameStDev() * team1RecencyModifier * team1GamesModifier;
                team1OPPGStdDev += sdr[i].getSDRTeam(team1).getPointsAllowedPerGameStDev() * team1RecencyModifier * team1GamesModifier;
                team1Count += team1RecencyModifier * team1GamesModifier;
            }
            if (sdr[i].hasTeam(team2)) {
                double team2RecencyModifier = Math.pow(RECENCY_BIAS, (sdr.length - i - 1) * (this.teams.get(team2).getNumberOfGames() == 0 ? 1 : this.teams.get(team2).getNumberOfGames()));
                double team2GamesModifier = 1 - Math.exp(-1 * sdr[i].getTeam(team2).getNumberOfGames());
                team2PPGStdDev += sdr[i].getSDRTeam(team2).getPointsPerGameStDev() * team2RecencyModifier * team2GamesModifier;
                team2OPPGStdDev += sdr[i].getSDRTeam(team2).getPointsAllowedPerGameStDev() * team2RecencyModifier * team2GamesModifier;
                team2Count += team2RecencyModifier * team2GamesModifier;
            }
        }
        team1PPGStdDev /= team1Count;
        team1OPPGStdDev /= team1Count;
        team2PPGStdDev /= team2Count;
        team2OPPGStdDev /= team2Count;

        if (Double.isNaN(team1PPGStdDev)) {
            team1PPGStdDev = ppgStdDev;
        }
        if (Double.isNaN(team1OPPGStdDev)) {
            team1OPPGStdDev = ppgStdDev;
        }
        if (Double.isNaN(team2PPGStdDev)) {
            team2PPGStdDev = ppgStdDev;
        }
        if (Double.isNaN(team2OPPGStdDev)) {
            team2OPPGStdDev = ppgStdDev;
        }

        double team1ProductionStdDev = (team1PPGStdDev + team2OPPGStdDev) / 2;
        double team2ProductionStdDev = (team2PPGStdDev + team1OPPGStdDev) / 2;

        double odds = cdf(0, team2Production - team1Production, Math.sqrt(Math.pow(team1ProductionStdDev, 2) + Math.pow(team2ProductionStdDev, 2)));

        return new Prediction(team1, team2, odds, team1Score, team2Score);
    }

    @Override
    protected String prettyPrintRankingsHeader(boolean allStats) {
        StringBuilder header = new StringBuilder();
        header.append("------------------------------------------------------------------------------------------------------------\n");
        if (week < 0) {
            header.append(Terminal.centerJustify(Integer.toString(year), 108));
        } else {
            header.append(Terminal.centerJustify(year + " Week " + week, 108));
        }
        header.append("\n------------------------------------------------------------------------------------------------------------\n");
        return header.toString();
    }

    @Override
    protected String prettyPrintColumnHeaders(boolean allStats) {
        return "     " + Terminal.leftJustify("Team", 50) + "   "
                + (!cumulative ? "Year   " : "")
                + Terminal.leftJustify("Rating", 10) + "   "
                + (allStats ? Terminal.leftJustify("Offense", 10) + "   "
                + Terminal.leftJustify("Defense", 10) + "   " : "")
                + Terminal.leftJustify("Record", 10);
    }

    @Override
    protected String printTeam(String team, boolean allStats) {
        return teams.get(team).getName() + "\t"
                + (!cumulative ? teams.get(team).getYear() + "\t" : "")
                + teams.get(team).getRating() + "\t"
                + (allStats ? teams.get(team).getOffensiveRating() + "\t"
                +  teams.get(team).getDefensiveRating() + "\t"
                +  teams.get(team).getQuad1Rating() + "\t"
                +  teams.get(team).getQuad2Rating() + "\t"
                +  teams.get(team).getQuad3Rating() + "\t"
                +  teams.get(team).getQuad4Rating() + "\t"
                +  teams.get(team).getHomeRating() + "\t"
                +  teams.get(team).getAwayRating() + "\t" : "")
                + teams.get(team).getRecord();
    }

    @Override
    protected String prettyPrintTeam(String team, boolean allStats) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + (!cumulative ? teams.get(team).getYear() + "   " : "")
                + Terminal.rightJustify(Terminal.round(teams.get(team).getRating(), 3), 10) + "   "
                + (allStats ? Terminal.rightJustify(Terminal.round(teams.get(team).getOffensiveRating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getDefensiveRating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getQuad1Rating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getQuad2Rating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getQuad3Rating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getQuad4Rating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getHomeRating(), 3), 10) + "   "
                + Terminal.rightJustify(Terminal.round(teams.get(team).getAwayRating(), 3), 10) + "   " : "")
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
    }

    public boolean hasTeam(String team) {
        return teams.containsKey(team);
    }



    //========== SimpleEfficiencyRating only methods ==========

    /**
     * Calculates the season efficiencies of every team
     */
    private void calculateEfficiencies() {
        double games = 0.0;
        for (String team : teams.keySet()) {
            double offensiveEfficiency = 0.0;
            double defensiveEfficiency = 0.0;
            double weightedCount = 0.0;
            for (int i = 0; i < sdr.length; i++) {
                if (sdr[i].hasTeam(team)) {
                    double recencyModifier = Math.pow(RECENCY_BIAS, (sdr.length - i - 1) * (this.teams.get(team).getNumberOfGames() == 0 ? 1 : this.teams.get(team).getNumberOfGames()));
                    double gamesModifier = 1 - Math.exp(-1 * sdr[i].getTeam(team).getNumberOfGames());
                    offensiveEfficiency += sdr[i].getSDRTeam(team).getOffensiveRating() * recencyModifier * gamesModifier;
                    defensiveEfficiency += sdr[i].getSDRTeam(team).getDefensiveRating() * recencyModifier * gamesModifier;
                    weightedCount += recencyModifier * gamesModifier;
                    for (Game game : sdr[i].getTeam(team).getGames()) {
                        ppgStdDev += Math.pow(game.getScore() - ppg, 2);
                        games++;
                    }
                }
            }
            teams.get(team).setOffensiveRating(offensiveEfficiency / weightedCount);
            teams.get(team).setDefensiveRating(defensiveEfficiency / weightedCount);
            teams.get(team).calculateRating();
        }
        ppgStdDev = Math.sqrt(ppgStdDev / games);
    }

    private static double pdf(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    private static double cdf(double z) {
        if (z < -8.0) return 0.0;
        if (z >  8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * pdf(z);
    }

    private static double cdf(double z, double mu, double sigma) {
        return cdf((z - mu) / sigma);
    }

}
