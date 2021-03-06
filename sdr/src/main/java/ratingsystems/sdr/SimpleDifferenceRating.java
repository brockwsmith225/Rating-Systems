package ratingsystems.sdr;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.*;

public class SimpleDifferenceRating extends RatingSystem {

    private double ppg;
    private Map<String, SDRTeam> teams;

    public SimpleDifferenceRating() {
        super();
    }

    public SimpleDifferenceRating(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SDRTeam(super.teams.get(team)));
        }
    }

    public SimpleDifferenceRating(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SDRTeam(super.teams.get(team)));
        }
    }

    public SimpleDifferenceRating(Interpreter interpreter, int[] years, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SDRTeam(super.teams.get(team)));
        }
    }

    public SimpleDifferenceRating(Interpreter interpreter, int[] years, int week, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, week, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SDRTeam(super.teams.get(team)));
        }
    }

    @Override
    public void setup() {
        ppg = 0.0;

        calculateDifferences();

        rankTeams();

        calculateOtherEfficiences();
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

        double team1OffensiveRating = teams.get(team1).getOffensiveRating();
        double team1DefensiveRating = teams.get(team1).getDefensiveRating();
        double team2OffensiveRating = teams.get(team2).getOffensiveRating();
        double team2DefensiveRating = teams.get(team2).getDefensiveRating();

        double team1Production = (team1OffensiveRating + team2DefensiveRating) / 2;
        double team2Production = (team2OffensiveRating + team1DefensiveRating) / 2;

        double team1Score = team1Production + ppg;
        double team2Score = team2Production + ppg;

        double team1PPGStdDev = teams.get(team1).getPointsPerGameStDev();
        double team1OPPGStdDev = teams.get(team1).getPointsAllowedPerGameStDev();
        double team2PPGStdDev = teams.get(team2).getPointsPerGameStDev();
        double team2OPPGStdDev = teams.get(team2).getPointsAllowedPerGameStDev();

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

    public SDRTeam getSDRTeam(String team) {
        if (teams.containsKey(team)) {
            return teams.get(team);
        }
        return null;
    }

    public boolean hasTeam(String team) {
        return teams.containsKey(team);
    }

    public double getPPG() {
        return ppg;
    }



    //========== SimpleEfficiencyRating only methods ==========

    /**
     * Calculates the season efficiencies of every team
     */
    private void calculateDifferences() {
        int count = 0;
        for (SDRTeam team : teams.values()) {
            int games = 0;
            double offensiveDifference = 0.0;
            double defensiveDifference = 0.0;
            for (Game game : team.getGames()) {
                games++;
                offensiveDifference += calculateOffensiveDifference(game);
                defensiveDifference += calculateDefensiveDifference(game);
                ppg += game.getScore();
                count++;
            }
            team.setOffensiveRating(offensiveDifference / games);
            team.setDefensiveRating(defensiveDifference / games);
            team.calculateRating();
        }
        ppg /= count;
    }

    private void calculateOtherEfficiences() {
        for (SDRTeam team : teams.values()) {
            double quad1Efficiency = 1.0;
            int quad1Games = 0;
            double quad2Efficiency = 1.0;
            int quad2Games = 0;
            double quad3Efficiency = 1.0;
            int quad3Games = 0;
            double quad4Efficiency = 1.0;
            int quad4Games = 0;
            double homeEfficiency = 1.0;
            int homeGames = 0;
            double awayEfficiency = 1.0;
            int awayGames = 0;
            for (Game game : team.getGames()) {
                double singleGameEff = calculateOffensiveDifference(game) / calculateDefensiveDifference(game);
                int opponentRank = 0;
                for (; opponentRank < rankedTeams.size(); opponentRank++) {
                    if (rankedTeams.get(opponentRank).getName().equals(game.getOpponent())) {
                        break;
                    }
                }
                if ((opponentRank < 30 && game.getLocation() == Location.HOME)
                        || (opponentRank < 50 && game.getLocation() == Location.NEUTRAL)
                        || (opponentRank < 75 && game.getLocation() == Location.AWAY)) {
                    quad1Efficiency *= singleGameEff;
                    quad1Games++;
                } else if ((opponentRank < 75 && game.getLocation() == Location.HOME)
                        || (opponentRank < 100 && game.getLocation() == Location.NEUTRAL)
                        || (opponentRank < 135 && game.getLocation() == Location.AWAY)) {
                    quad2Efficiency *= singleGameEff;
                    quad2Games++;
                } else if ((opponentRank < 160 && game.getLocation() == Location.HOME)
                        || (opponentRank < 200 && game.getLocation() == Location.NEUTRAL)
                        || (opponentRank < 240 && game.getLocation() == Location.AWAY)) {
                    quad3Efficiency *= singleGameEff;
                    quad3Games++;
                } else {
                    quad4Efficiency *= singleGameEff;
                    quad4Games++;
                }
                if (game.getLocation() == Location.HOME) {
                    homeEfficiency *= singleGameEff;
                    homeGames++;
                } else {
                    awayEfficiency *= singleGameEff;
                    awayGames++;
                }
            }
            if (quad1Games > 0) {
                team.setQuad1Rating(Math.pow(quad1Efficiency, 1.0 / quad1Games));
            } else {
                team.setQuad1Rating(1.0);
            }
            if (quad2Games > 0) {
                team.setQuad2Rating(Math.pow(quad2Efficiency, 1.0 / quad2Games));
            } else {
                team.setQuad2Rating(1.0);
            }
            if (quad3Games > 0) {
                team.setQuad3Rating(Math.pow(quad3Efficiency, 1.0 / quad3Games));
            } else {
                team.setQuad3Rating(1.0);
            }
            if (quad4Games > 0) {
                team.setQuad4Rating(Math.pow(quad4Efficiency, 1.0 / quad4Games));
            } else {
                team.setQuad4Rating(1.0);
            }
            if (homeGames > 0) {
                team.setHomeRating(Math.pow(homeEfficiency, 1.0 / homeGames));
            } else {
                team.setHomeRating(1.0);
            }
            if (awayGames > 0) {
                team.setAwayRating(Math.pow(awayEfficiency, 1.0 / awayGames));
            } else {
                team.setAwayRating(1.0);
            }
        }
    }

    /**
     * Calculates the offensive efficiency of a single team during a single game
     *
     * @param game the game for which the offensive efficiency will be calculated
     * @return the offensive efficiency of the team from the game
     */
    private double calculateOffensiveDifference(Game game) {
        double offensiveDifference = game.getScore() - teams.get(game.getOpponent()).getPointsAllowedPerGame();
        return offensiveDifference;
    }

    /**
     * Calculates the defensive efficiency of a single team during a single game
     *
     * @param game the game for which the defensive efficiency will be calculated
     * @return the defensive efficiency of the team from the game
     */
    private double calculateDefensiveDifference(Game game) {
        double defensiveDifference = game.getOpponentScore() - teams.get(game.getOpponent()).getPointsPerGame();
        return defensiveDifference;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-1 * x));
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
