package ratingsystems.hps;

import ratingsystems.common.cli.Terminal;
import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Location;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.common.linalg.Vector;
import ratingsystems.ser.SimpleEfficiencyRating;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricalPredictionSystem extends RatingSystem {

    private Map<String, HPSTeam> teams;
    private Map<Integer, HashMap<String, HPSTeam>> allTeams;
    private SimpleEfficiencyRating[] ser;
    private boolean hasRankedTeams, hasRankedGroups;

    public HistoricalPredictionSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        hasRankedTeams = false;
        hasRankedGroups = false;
        teams = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        for (String team : super.teams.keySet()) {
            teams.put(team, new HPSTeam(super.teams.get(team)));
            allTeams.get(year).put(team, teams.get(team));
        }
        for (int y = 2000; y < 2100; y++) {
            if (y != year && interpreter.hasData(y)) {
                Map<String, Team> temp = interpreter.parseData(y);
                allTeams.put(y, new HashMap<>());
                for (String team : temp.keySet()) {
                    allTeams.get(y).put(team, new HPSTeam(temp.get(team)));
                }
            }
        }
        week = getWeek();
    }

    public HistoricalPredictionSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        hasRankedTeams = false;
        hasRankedGroups = false;
        teams = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        for (String team : super.teams.keySet()) {
            teams.put(team, new HPSTeam(super.teams.get(team)));
            allTeams.get(year).put(team, teams.get(team));
        }
        for (int y = 2000; y < 2100; y++) {
            if (y != year && interpreter.hasData(y)) {
                Map<String, Team> temp = interpreter.parseData(y);
                allTeams.put(y, new HashMap<>());
                for (String team : temp.keySet()) {
                    allTeams.get(y).put(team, new HPSTeam(temp.get(team)));
                }
            }
        }
    }

    @Override
    public void setup() {
        for (Integer year : allTeams.keySet()) {
            for (String team : allTeams.get(year).keySet()) {
                allTeams.get(year).get(team).setScaledStats(allTeams.get(year));
            }
        }

        int prevYears = 3;
        try {
            if (this.interpreter.hasData(this.year) && week > 0) {
                ser = new SimpleEfficiencyRating[prevYears + 1];
                for (int i = 0; i < ser.length - 1; i++) {
                    this.ser[i] = new SimpleEfficiencyRating(this.interpreter, this.year - (prevYears - i));
                    this.ser[i].setup();
                }
                this.ser[ser.length - 1] = new SimpleEfficiencyRating(this.interpreter, this.year, this.week);
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
    }

    @Override
    public void rankTeams() {
        calculateTeamRankings();
        super.rankTeams();
    }

    @Override
    public List<Team> getTeamRankings() {
        calculateTeamRankings();
        return List.copyOf(rankedTeams);
    }

    @Override
    public String printTeamRankings(boolean prettyPrint, boolean allStats) {
        if (!hasRankedTeams) {
            rankTeams();
        }
        return super.printTeamRankings(prettyPrint, allStats);
    }

    @Override
    protected String prettyPrintColumnHeaders(boolean allStats) {
        StringBuilder header = new StringBuilder();
        header.append("     " + Terminal.leftJustify("Team", 50) + "   " + Terminal.leftJustify("Rating", 10) + "   " + Terminal.leftJustify("Offense", 10) + "   " + Terminal.leftJustify("Defense", 10) + "   " + Terminal.leftJustify("Record", 10));
        return header.toString();
    }

    @Override
    protected String prettyPrintTeam(String team, boolean allStats) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getRating()), 10) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getOffensiveRating()), 10) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getDefensiveRating()), 10) + "   "
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        // VARIABLES
        int statNumRecentYears = 1;
        double statRecencyBias = 0.9;

        double locationBias = 0.0;

        double historicalResultsRecencyBias = 0.99;
        double minSimilarity = 0.0;
        double minGames = 10;

        double serRecencyBias = 0.9;
        double serPow = 1.0;



        Map<Integer, Double> team1Years = new HashMap<>();
        Map<Integer, Double> team2Years = new HashMap<>();
        double team1ModifiedCount = 0.0;
        double team2ModifiedCount = 0.0;

//        double team1OffEff = 1.0;
//        double team1DefEff = 1.0;
//        double team2OffEff = 1.0;
//        double team2DefEff = 1.0;
        for (int year = this.year; year >= this.year - statNumRecentYears; year--) {
            if (allTeams.containsKey(year)) {
                double recencyModifier = Math.pow(statRecencyBias, (this.year - year) * (this.week == 0 ? 1 : this.week));
                if (allTeams.get(year).containsKey(team1) && allTeams.get(year).get(team1).getNumberOfGames() > 0) {
                    team1Years.put(year, recencyModifier);
                    team1ModifiedCount += recencyModifier;
//                    team1OffEff *= Math.pow(allTeams.get(year).get(team1).offenseEfficiency(), recencyModifier);
//                    team1DefEff *= Math.pow(allTeams.get(year).get(team1).defenseEfficiency(), recencyModifier);
                }
                if (allTeams.get(year).containsKey(team2) && allTeams.get(year).get(team2).getNumberOfGames() > 0) {
                    team2Years.put(year, recencyModifier);
                    team2ModifiedCount += recencyModifier;
//                    team2OffEff *= Math.pow(allTeams.get(year).get(team2).offenseEfficiency(), recencyModifier);
//                    team2DefEff *= Math.pow(allTeams.get(year).get(team2).defenseEfficiency(), recencyModifier);
                }
            }
        }
//        team1OffEff = Math.pow(team1OffEff, 1.0 / team1ModifiedCount);
//        team1DefEff = Math.pow(team1DefEff, 1.0 / team1ModifiedCount);
//        team2OffEff = Math.pow(team2OffEff, 1.0 / team2ModifiedCount);
//        team2DefEff = Math.pow(team2DefEff, 1.0 / team2ModifiedCount);

        Map<String, Double> team1Similarities = new HashMap<>();
        Map<String, Double> team2Similarities = new HashMap<>();
        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    team1Similarities.put(historicalTeam, 0.0);
                    for (Integer team1Year : team1Years.keySet()) {
                        team1Similarities.put(historicalTeam, team1Similarities.get(historicalTeam) + team1Years.get(team1Year) * allTeams.get(team1Year).get(team1).similarity(allTeams.get(year).get(historicalTeam)));
                    }
                    team1Similarities.put(historicalTeam, team1Similarities.get(historicalTeam) / team1ModifiedCount);
                    team2Similarities.put(historicalTeam, 0.0);
                    for (Integer team2Year : team2Years.keySet()) {
                        team2Similarities.put(historicalTeam, team2Similarities.get(historicalTeam) + team2Years.get(team2Year) * allTeams.get(team2Year).get(team2).similarity(allTeams.get(year).get(historicalTeam)));
                    }
                    team2Similarities.put(historicalTeam, team2Similarities.get(historicalTeam) / team2ModifiedCount);
                }
            }
        }

        ArrayList<Double> gameSimilarities = new ArrayList<>();
        ArrayList<Double> team1Scores = new ArrayList<>();
        ArrayList<Double> team2Scores = new ArrayList<>();
        int gamesFound = 0;
        double avgScore = 0.0;
        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                double recencyModifier = Math.pow(historicalResultsRecencyBias, Math.abs(this.year - year));
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    for (Game game : allTeams.get(year).get(historicalTeam).getGames()) {
                        double locationModifier = location == game.getLocation() ? 1.0 : location == Location.NEUTRAL || game.getLocation() == Location.NEUTRAL ? locationBias : locationBias * locationBias;
                        double gameSimilarity = team1Similarities.get(historicalTeam) * team2Similarities.get(game.getOpponent()) * recencyModifier * locationModifier;
                        if (gameSimilarity >= minSimilarity) {
                            gameSimilarities.add(gameSimilarity);
                            gamesFound++;
                        } else {
                            gameSimilarities.add(0.0);
                        }
                        team1Scores.add(game.getScore());
                        team2Scores.add(game.getOpponentScore());
                        avgScore += game.getScore();
                    }
                }
            }
        }
        avgScore /= gameSimilarities.size();

        Vector gameSimilaritiesVector = new Vector(gameSimilarities);
//        double cutoff = gameSimilaritiesVector.percentile(0.9);
//        for (int i = 0; i < gameSimilaritiesVector.size(); i++) {
//            if (gameSimilaritiesVector.get(i) < cutoff) {
//                gameSimilaritiesVector.set(i, 0.0);
//            }
//        }
        gameSimilaritiesVector = gameSimilaritiesVector.modifiedSoftmax();

        Vector team1ScoresVector = new Vector(team1Scores);
        Vector team2ScoresVector = new Vector(team2Scores);

        double team1Off = 0.0;
        double team1Def = 0.0;
        double team2Off = 0.0;
        double team2Def = 0.0;
        team1ModifiedCount = 0.0;
        team2ModifiedCount = 0.0;
        for (int i = 0; i < ser.length; i++) {
            double recencyModifier = Math.pow(serRecencyBias, (ser.length - i - 1) * (this.week == 0 ? 1 : this.week));
            if (ser[i].hasTeam(team1) && ser[i].getTeam(team1).getNumberOfGames() > 0) {
                team1Off += ser[i].getSERTeam(team1).getOffensiveRating() * recencyModifier;
                team1Def += ser[i].getSERTeam(team1).getDefensiveRating() * recencyModifier;
                team1ModifiedCount += recencyModifier;
            }
            if (ser[i].hasTeam(team2) && ser[i].getTeam(team2).getNumberOfGames() > 0) {
                team2Off += ser[i].getSERTeam(team2).getOffensiveRating() * recencyModifier;
                team2Def += ser[i].getSERTeam(team2).getDefensiveRating() * recencyModifier;
                team2ModifiedCount += recencyModifier;
            }
        }

        team1Off /= team1ModifiedCount;
        team1Def /= team1ModifiedCount;
        team2Off /= team2ModifiedCount;
        team2Def /= team2ModifiedCount;

        if (team1Off == 0.0 || Double.isNaN(team1Off)) team1Off = 1.0;
        if (team1Def == 0.0 || Double.isNaN(team1Def)) team1Def = 1.0;
        if (team2Off == 0.0 || Double.isNaN(team2Off)) team2Off = 1.0;
        if (team2Def == 0.0 || Double.isNaN(team2Def)) team2Def = 1.0;

        double team1RawScore = avgScore;
        double team2RawScore = avgScore;
        if (gamesFound >= minGames) {
            team1RawScore = gameSimilaritiesVector.dotProduct(team1ScoresVector);
            team2RawScore = gameSimilaritiesVector.dotProduct(team2ScoresVector);
        }
        if (Double.isNaN(team1RawScore)) team1RawScore = avgScore;
        if (Double.isNaN(team2RawScore)) team2RawScore = avgScore;

//        System.out.println(team1RawScore + " " + Math.pow(team1Off / team2Def, serPow));
//        System.out.println(team2RawScore + " " + Math.pow(team2Off / team1Def, serPow));
        double team1ExpectedScore = team1RawScore * Math.pow(team1Off / team2Def, serPow);
        double team2ExpectedScore = team2RawScore * Math.pow(team2Off / team1Def, serPow);

        //double odds = team1ExpectedScore / (team1ExpectedScore + team2ExpectedScore);
        double odds = 1.0 / (1.0 + Math.exp(4 * (team2ExpectedScore - team1ExpectedScore) / (team1ExpectedScore + team2ExpectedScore)));

        return new Prediction(team1, team2, odds, team1ExpectedScore, team2ExpectedScore, location);
    }


    //========== HistoricalPredictionSystem only methods ==========

    private void calculateTeamRankings() {
        hasRankedTeams = true;
        HashMap<String, Double> ratings = new HashMap<>();
        HashMap<String, Double> offRatings = new HashMap<>();
        HashMap<String, Double> defRatings = new HashMap<>();
//        for (String team : allTeams.get(this.year).keySet()) {
//            ratings.put(team, 0.0);
//            offRatings.put(team, 0.0);
//            defRatings.put(team, 0.0);
//            for (String opponent : allTeams.get(this.year).keySet()) {
//                if (!team.equals(opponent)) {
//                    Prediction prediction = predictGame(team, opponent, Location.NEUTRAL);
//                    ratings.put(team, ratings.get(team) + prediction.getLine() * -1);
//                    offRatings.put(team, offRatings.get(team) + prediction.getTeam1Score());
//                    defRatings.put(team, defRatings.get(team) + prediction.getTeam2Score());
//                }
//            }
//        }
//
//        for (String team : allTeams.get(year).keySet()) {
//            allTeams.get(this.year).get(team).setRating(ratings.get(team) / (ratings.size() - 1));
//            allTeams.get(this.year).get(team).setOffensiveRating(offRatings.get(team) / (offRatings.size() - 1));
//            allTeams.get(this.year).get(team).setDefensiveRating(defRatings.get(team) / (defRatings.size() - 1));
//        }
        for (String team : allTeams.get(this.year).keySet()) {
            ratings.put(team, 0.0);
            Prediction prediction = predictVersusAverage(team, Location.NEUTRAL);
            ratings.put(team, prediction.getLine() * -1);
            offRatings.put(team, prediction.getTeam1Score());
            defRatings.put(team, prediction.getTeam2Score());
        }

        for (String team : allTeams.get(year).keySet()) {
            allTeams.get(this.year).get(team).setRating(ratings.get(team));
            allTeams.get(this.year).get(team).setOffensiveRating(offRatings.get(team));
            allTeams.get(this.year).get(team).setDefensiveRating(defRatings.get(team));
        }
    }

    public Prediction predictVersusAverage(String team1, Location location) {
        // VARIABLES
        int statNumRecentYears = 1;
        double statRecencyBias = 0.9;

        double locationBias = 0.0;

        double historicalResultsRecencyBias = 0.99;
        double minSimilarity = 0.0;
        double minGames = 10;

        double serRecencyBias = 0.9;
        double serPow = 1.0;



        Map<Integer, Double> team1Years = new HashMap<>();
        double team1ModifiedCount = 0.0;

        for (int year = this.year; year >= this.year - statNumRecentYears; year--) {
            if (allTeams.containsKey(year)) {
                double recencyModifier = Math.pow(statRecencyBias, (this.year - year) * (this.week == 0 ? 1 : this.week));
                if (allTeams.get(year).containsKey(team1)) {
                    team1Years.put(year, recencyModifier);
                    team1ModifiedCount += recencyModifier;
                }
            }
        }

        Map<String, Double> team1Similarities = new HashMap<>();
        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    team1Similarities.put(historicalTeam, 0.0);
                    for (Integer team1Year : team1Years.keySet()) {
                        team1Similarities.put(historicalTeam, team1Similarities.get(historicalTeam) + team1Years.get(team1Year) * allTeams.get(team1Year).get(team1).similarity(allTeams.get(year).get(historicalTeam)));
                    }
                    team1Similarities.put(historicalTeam, team1Similarities.get(historicalTeam) / team1ModifiedCount);
                }
            }
        }

        ArrayList<Double> gameSimilarities = new ArrayList<>();
        ArrayList<Double> team1Scores = new ArrayList<>();
        int gamesFound = 0;
        double avgScore = 0.0;
        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                double recencyModifier = Math.pow(historicalResultsRecencyBias, Math.abs(this.year - year));
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    for (Game game : allTeams.get(year).get(historicalTeam).getGames()) {
                        double locationModifier = location == game.getLocation() ? 1.0 : location == Location.NEUTRAL || game.getLocation() == Location.NEUTRAL ? locationBias : locationBias * locationBias;
                        double gameSimilarity = team1Similarities.get(historicalTeam) * recencyModifier * locationModifier;
                        if (gameSimilarity >= minSimilarity) {
                            gameSimilarities.add(gameSimilarity);
                            gamesFound++;
                        } else {
                            gameSimilarities.add(0.0);
                        }
                        team1Scores.add(game.getScore());
                        avgScore += game.getScore();
                    }
                }
            }
        }
        avgScore /= gameSimilarities.size();

        Vector gameSimilaritiesVector = new Vector(gameSimilarities);
        gameSimilaritiesVector = gameSimilaritiesVector.modifiedSoftmax();

        Vector team1ScoresVector = new Vector(team1Scores);

        double team1Off = 0.0;
        double team1Def = 0.0;
        team1ModifiedCount = 0.0;
        for (int i = 0; i < ser.length; i++) {
            double recencyModifier = Math.pow(serRecencyBias, (ser.length - i - 1) * (this.week == 0 ? 1 : this.week));
            if (ser[i].hasTeam(team1)) {
                team1Off += ser[i].getSERTeam(team1).getOffensiveRating() * recencyModifier;
                team1Def += ser[i].getSERTeam(team1).getDefensiveRating() * recencyModifier;
                team1ModifiedCount += recencyModifier;
            }
        }

        team1Off /= team1ModifiedCount;
        team1Def /= team1ModifiedCount;

        if (team1Off == 0.0 || Double.isNaN(team1Off)) team1Off = 1.0;
        if (team1Def == 0.0 || Double.isNaN(team1Def)) team1Def = 1.0;

        double team1RawScore = avgScore;
        double team2RawScore = avgScore;
        if (gamesFound >= minGames) {
            team1RawScore = gameSimilaritiesVector.dotProduct(team1ScoresVector);
            team2RawScore = avgScore;
        }
        if (Double.isNaN(team1RawScore)) team1RawScore = avgScore;

        double team1ExpectedScore = team1RawScore * Math.pow(team1Off, serPow);
        double team2ExpectedScore = team2RawScore * Math.pow(1.0 / team1Def, serPow);

        double odds = 1.0 / (1.0 + Math.exp(4 * (team2ExpectedScore - team1ExpectedScore) / (team1ExpectedScore + team2ExpectedScore)));

        return new Prediction(team1, "AVERAGE", odds, team1ExpectedScore, team2ExpectedScore, location);
    }

}
