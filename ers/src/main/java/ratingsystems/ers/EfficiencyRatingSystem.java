package ratingsystems.ers;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.ser.SimpleEfficiencyRating;
import ratingsystems.ser.SERTeam;

import java.io.FileNotFoundException;
import java.util.*;

public class EfficiencyRatingSystem extends RatingSystem {

    private double ppg;
    private Map<String, ERSTeam> teams;
    private SimpleEfficiencyRating[] ser;

    public EfficiencyRatingSystem() {
        super();
    }

    public EfficiencyRatingSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new ERSTeam(super.teams.get(team)));
        }
    }

    public EfficiencyRatingSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new ERSTeam(super.teams.get(team)));
        }
    }

    public EfficiencyRatingSystem(Interpreter interpreter, int[] years, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new ERSTeam(super.teams.get(team)));
        }
    }

    public EfficiencyRatingSystem(Interpreter interpreter, int[] years, int week, boolean cumulative) throws FileNotFoundException {
        super(interpreter, years, week, cumulative);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new ERSTeam(super.teams.get(team)));
        }
    }

    @Override
    public void setup() {
        int prevYears = 3;
        try {
            if (this.interpreter.hasData(this.year) && this.week != 0) {
                ser = new SimpleEfficiencyRating[prevYears + 1];
                for (int i = 0; i < ser.length - 1; i++) {
                    this.ser[i] = new SimpleEfficiencyRating(this.interpreter, this.year - (prevYears - i));
                    this.ser[i].setup();
                }
                if (this.week == -1) {
                    this.ser[ser.length - 1] = new SimpleEfficiencyRating(this.interpreter, this.year);
                } else {
                    this.ser[ser.length - 1] = new SimpleEfficiencyRating(this.interpreter, this.year, this.week);
                }
                this.ser[ser.length - 1].setup();
            } else {
                ser = new SimpleEfficiencyRating[prevYears];
                for (int i = 0; i < ser.length; i++) {
                    this.ser[i] = new SimpleEfficiencyRating(this.interpreter, this.year - (prevYears - i));
                    this.ser[i].setup();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ppg = ser[ser.length - 1].getPPG();

        calculateEfficiencies();

        rankTeams();

        calculateOtherEfficiences();
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        if (!teams.keySet().contains(team1) || !teams.keySet().contains(team2)) {
            return new Prediction(team1, team2, 0.5);
        }

        double team1OffensiveEfficiency = teams.get(team1).getOffensiveEfficiency();
        double team1DefensiveEfficiency = teams.get(team1).getDefensiveEfficiency();
        double team2OffensiveEfficiency = teams.get(team2).getOffensiveEfficiency();
        double team2DefensiveEfficiency = teams.get(team2).getDefensiveEfficiency();

        double team1Production = Math.sqrt(team1OffensiveEfficiency / team2DefensiveEfficiency);
        double team2Production = Math.sqrt(team2OffensiveEfficiency / team1DefensiveEfficiency);

        double team1Score = team1Production * ppg;
        double team2Score = team2Production * ppg;

        double odds = cdf(0, Math.log(team2Production) - Math.log(team1Production), 0.6);

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
        for (String team : teams.keySet()) {
            double offensiveEfficiency = 1.0;
            double defensiveEfficiency = 1.0;
            double weightedCount = 1.0;
            for (int i = 0; i < ser.length; i++) {
                double recencyModifier = Math.pow(0.9, (ser.length - i - 1) * (this.teams.get(team).getNumberOfGames() == 0 ? 1 : this.teams.get(team).getNumberOfGames()));
                if (ser[i].hasTeam(team)) {
                    offensiveEfficiency *= Math.pow(ser[i].getSERTeam(team).getOffensiveRating(), recencyModifier);
                    defensiveEfficiency *= Math.pow(ser[i].getSERTeam(team).getDefensiveRating(), recencyModifier);
                    weightedCount += recencyModifier;
                }
            }
            teams.get(team).setOffensiveEfficiency(Math.pow(offensiveEfficiency, 1.0 / weightedCount));
            teams.get(team).setDefensiveEfficiency(Math.pow(defensiveEfficiency, 1.0 / weightedCount));
            teams.get(team).calculateEfficiency();
            teams.get(team).setOffensiveRating(ppg * teams.get(team).getOffensiveEfficiency());
            teams.get(team).setDefensiveRating(ppg / teams.get(team).getDefensiveEfficiency());
            teams.get(team).calculateRating();
        }
    }

    private void calculateOtherEfficiences() {
        for (SERTeam team : teams.values()) {
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
                double singleGameEff = calculateOffensiveEfficiency(game) / calculateDefensiveEfficiency(game);
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
    private double calculateOffensiveEfficiency(Game game) {
        double offensiveEfficiency = game.getScore() / teams.get(game.getOpponent()).getPointsAllowedPerGame();
        if (Double.isNaN(offensiveEfficiency)) return 1.0;
        return Math.exp(offensiveEfficiency - 1);
    }

    /**
     * Calculates the defensive efficiency of a single team during a single game
     *
     * @param game the game for which the defensive efficiency will be calculated
     * @return the defensive efficiency of the team from the game
     */
    private double calculateDefensiveEfficiency(Game game) {
        double defensiveEfficiency = game.getOpponentScore() / teams.get(game.getOpponent()).getPointsPerGame();
        if (Double.isNaN(defensiveEfficiency)) return 1.0;
        return 1.0 / Math.exp(defensiveEfficiency - 1);
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
