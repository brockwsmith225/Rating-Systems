package ratingsystems.srs;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;

import java.io.FileNotFoundException;
import java.util.*;

public class SampleRatingSystem extends RatingSystem {

    private double ppg;
    private Map<String, SRSTeam> teams;

    public SampleRatingSystem() {
        super();
    }

    public SampleRatingSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SRSTeam(super.teams.get(team)));
        }
    }

    public SampleRatingSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SRSTeam(super.teams.get(team)));
        }
    }

    public SampleRatingSystem(Interpreter interpreter, int[] years, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SRSTeam(super.teams.get(team)));
        }
    }

    public SampleRatingSystem(Interpreter interpreter, int[] years, int week, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, week, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new SRSTeam(super.teams.get(team)));
        }
    }

    @Override
    public void setup() {
        ppg = 0.0;

//        calculateRatings();

//        rankTeams();

//        calculateOtherEfficiences();
    }

    @Override
    public void rankTeams() {
        rankedTeams = new ArrayList<>(teams.values());
        Collections.sort(rankedTeams);
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        boolean debug = false;

        if (debug) System.out.println(team1 + " vs " + team2);
        if (!teams.keySet().contains(team1) || !teams.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        double team1OffenseMean = teams.get(team1).getAdjustedPointsPerGame(this);
        double team1OffenseStdDev = teams.get(team1).getAdjustedPointsPerGameStdDev(this);
        double team1DefenseMean = teams.get(team1).getAdjustedPointsAllowedPerGame(this);
        double team1DefenseStdDev = teams.get(team1).getAdjustedPointsAllowedPerGameStdDev(this);
        if (debug) System.out.println("Team 1 Offense = " + team1OffenseMean);
        if (debug) System.out.println("Team 1 Defense = " + team1DefenseMean);

        double team2OffenseMean = teams.get(team2).getAdjustedPointsPerGame(this);
        double team2OffenseStdDev = teams.get(team2).getAdjustedPointsPerGameStdDev(this);
        double team2DefenseMean = teams.get(team2).getAdjustedPointsAllowedPerGame(this);
        double team2DefenseStdDev = teams.get(team2).getAdjustedPointsAllowedPerGameStdDev(this);
        if (debug) System.out.println("Team 2 Offense = " + team2OffenseMean);
        if (debug) System.out.println("Team 2 Defense = " + team2DefenseMean);

        if (Double.isNaN(team1OffenseMean) || Double.isNaN(team1DefenseMean) || Double.isNaN(team2OffenseMean) || Double.isNaN(team2DefenseMean)) {
            return new Prediction(team1, team2, 0.5);
        }

        double team1ProductionMean = team1OffenseMean + team2DefenseMean;
        double team2ProductionMean = team2OffenseMean + team1DefenseMean;
        if (debug) System.out.println("Team 1 Production = " + team1ProductionMean);
        if (debug) System.out.println("Team 2 Production = " + team2ProductionMean);

        double line = team1ProductionMean - team2ProductionMean;
        if (debug) System.out.println("Line = " + line);

        double team1TotalMean = teams.get(team1).getPointsPerGame() + teams.get(team1).getPointsAllowedPerGame();
        double team2TotalMean = teams.get(team2).getPointsPerGame() + teams.get(team2).getPointsAllowedPerGame();
        double totalPoints = (team1TotalMean + team2TotalMean) / 2.0;
        if (debug) System.out.println("Total Points = " + totalPoints);

        double team1ProductionStdDev = Math.sqrt(Math.pow(team1OffenseStdDev, 2) + Math.pow(team2DefenseStdDev, 2));
        double team2ProductionStdDev = Math.sqrt(Math.pow(team2OffenseStdDev, 2) + Math.pow(team1DefenseStdDev, 2));
        double totalProductionStdDev = Math.sqrt(Math.pow(team1ProductionStdDev, 2) + Math.pow(team2ProductionStdDev, 2));
        if (debug) System.out.println("Total Std Dev = " + totalProductionStdDev);

        if (totalProductionStdDev == 0.0) {
            return new Prediction(team1, team2, 0.5);
        }

        double odds = 1 - cdf(0, line, totalProductionStdDev);

        return new Prediction(team1, team2, odds, (totalPoints + line) / 2.0, (totalPoints - line) / 2.0);
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

    public SRSTeam getSRSTeam(String team) {
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
    private void calculateRatings() {
//        for (String team : teams.keySet()) {
//            double rating = 0.0;
//            double offensiveRating = 0.0;
//            double defensiveRating = 0.0;
//            for (String opponent : teams.keySet()) {
//                Prediction prediction = predictGame(team, opponent, Location.NEUTRAL);
//                rating += prediction.getLine();
//                offensiveRating += prediction.getTeam1Score();
//                defensiveRating += prediction.getTeam2Score();
//            }
//            teams.get(team).setRating(rating / teams.size());
//            teams.get(team).setOffensiveRating(offensiveRating / teams.size());
//            teams.get(team).setDefensiveRating(defensiveRating / teams.size());
//        }
        for (SRSTeam team : teams.values()) {
            double offenseMean = team.getAdjustedPointsPerGame(this);
            double defenseMean = team.getAdjustedPointsAllowedPerGame(this);
            team.setRating(offenseMean - defenseMean);
        }
    }

    private void calculateOtherEfficiences() {
        for (SRSTeam team : teams.values()) {
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
