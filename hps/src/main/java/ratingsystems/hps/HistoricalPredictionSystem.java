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

    public HistoricalPredictionSystem(Interpreter interpreter, int[] years) throws FileNotFoundException {
        super(interpreter, years, false);
        hasRankedTeams = false;
        hasRankedGroups = false;
        teams = new HashMap<>();
        allTeams = new HashMap<>();
        for (int y : years) {
            allTeams.put(y, new HashMap<>());
        }
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new HPSTeam(super.teams.get(team)));
            allTeams.get(teams.get(team).getYear()).put(team, teams.get(team));
        }
        for (int y = 2000; y < 2100; y++) {
            if ((y < years[0] || y > years[years.length-1]) && interpreter.hasData(y)) {
                Map<String, Team> temp = interpreter.parseData(y);
                allTeams.put(y, new HashMap<>());
                for (String team : temp.keySet()) {
                    allTeams.get(y).put(team, new HPSTeam(temp.get(team)));
                }
            }
        }
    }

    public HistoricalPredictionSystem(Interpreter interpreter, int[] years, int week) throws FileNotFoundException {
        super(interpreter, years, week, false);
        this.teams = new HashMap<>();
        for (String team : super.teams.keySet()) {
            this.teams.put(team, new HPSTeam(super.teams.get(team)));
            allTeams.get(teams.get(team).getYear()).put(team, teams.get(team));
        }
        for (int y = 2000; y < 2100; y++) {
            if ((y < years[0] || y > years[years.length-1]) && interpreter.hasData(y)) {
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
        calculateOtherTeamRankings();
    }

    @Override
    public List<Team> getTeamRankings() {
        rankTeams();
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
    protected String printTeam(String team, boolean allStats) {
        return teams.get(team).getName() + "\t"
                + (!cumulative ? teams.get(team).getYear() + "\t" : "")
                + teams.get(team).getRating() + "\t"
                + (allStats ? teams.get(team).getOffensiveRating() + "\t"
                + teams.get(team).getDefensiveRating() + "\t"
                + teams.get(team).getTop25Rating() + "\t"
                + teams.get(team).getTop5Rating() + "\t"
                + teams.get(team).getHomeRating() + "\t"
                + teams.get(team).getAwayRating() + "\t" : "")
                + teams.get(team).getRecord();
    }

    @Override
    protected String prettyPrintTeam(String team, boolean allStats) {
        return Terminal.leftJustify(teams.get(team).getName(), 50) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getRating()), 10) + "   "
                + (!cumulative ? teams.get(team).getYear() + "   " : "")
                + (allStats ? Terminal.rightJustify(Double.toString(teams.get(team).getOffensiveRating()), 10) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getDefensiveRating()), 10) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getTop25Rating()), 10) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getTop5Rating()), 10) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getHomeRating()), 10) + "   "
                + Terminal.rightJustify(Double.toString(teams.get(team).getAwayRating()), 10) + "   " : "")
                + Terminal.rightJustify(teams.get(team).getRecord(), 10);
    }

    @Override
    public Prediction predictGame(String team1, String team2, Location location) {
        if (!teams.containsKey(team1) && !allTeams.get(this.year-1).containsKey(team1)) {
            System.out.println("Team not found " + team1);
        }
        if (!teams.containsKey(team2) && !allTeams.get(this.year-1).containsKey(team2)) {
            System.out.println("Team not found " + team2);
        }

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
                double team1RecencyModifier = Math.pow(statRecencyBias, (this.year - year) * (this.teams.get(team1).getNumberOfGames() == 0 ? 1 : this.teams.get(team1).getNumberOfGames()));
                double team2RecencyModifier = Math.pow(statRecencyBias, (this.year - year) * (this.teams.get(team2).getNumberOfGames() == 0 ? 1 : this.teams.get(team2).getNumberOfGames()));
                if (allTeams.get(year).containsKey(team1) && allTeams.get(year).get(team1).getNumberOfGames() > 0) {
                    team1Years.put(year, team1RecencyModifier);
                    team1ModifiedCount += team1RecencyModifier;
//                    team1OffEff *= Math.pow(allTeams.get(year).get(team1).offenseEfficiency(), recencyModifier);
//                    team1DefEff *= Math.pow(allTeams.get(year).get(team1).defenseEfficiency(), recencyModifier);
                }
                if (allTeams.get(year).containsKey(team2) && allTeams.get(year).get(team2).getNumberOfGames() > 0) {
                    team2Years.put(year, team2RecencyModifier);
                    team2ModifiedCount += team2RecencyModifier;
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
            double team1RecencyModifier = Math.pow(serRecencyBias, (ser.length - i - 1) * (this.teams.get(team1).getNumberOfGames() == 0 ? 1 : this.teams.get(team1).getNumberOfGames()));
            double team2RecencyModifier = Math.pow(serRecencyBias, (ser.length - i - 1) * (this.teams.get(team2).getNumberOfGames() == 0 ? 1 : this.teams.get(team2).getNumberOfGames()));
            if (ser[i].hasTeam(team1) && ser[i].getTeam(team1).getNumberOfGames() > 0) {
                team1Off += ser[i].getSERTeam(team1).getOffensiveRating() * team1RecencyModifier;
                team1Def += ser[i].getSERTeam(team1).getDefensiveRating() * team1RecencyModifier;
                team1ModifiedCount += team1RecencyModifier;
            }
            if (ser[i].hasTeam(team2) && ser[i].getTeam(team2).getNumberOfGames() > 0) {
                team2Off += ser[i].getSERTeam(team2).getOffensiveRating() * team2RecencyModifier;
                team2Def += ser[i].getSERTeam(team2).getDefensiveRating() * team2RecencyModifier;
                team2ModifiedCount += team2RecencyModifier;
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

//        double odds = team1ExpectedScore / (team1ExpectedScore + team2ExpectedScore);
        double odds = 1.0 / (1.0 + Math.exp(4 * (team2ExpectedScore - team1ExpectedScore) / (team1ExpectedScore + team2ExpectedScore)));

        return new Prediction(team1, team2, odds, team1ExpectedScore, team2ExpectedScore, location);
    }

    public boolean hasTeam(String team) {
        return teams.containsKey(team);
    }



    //========== HistoricalPredictionSystem only methods ==========

    private void calculateTeamRankings() {
        hasRankedTeams = true;
        for (String team : teams.keySet()) {
            Prediction prediction = predictVersusAverage(team, Location.NEUTRAL);
            teams.get(team).setRating(prediction.getLine() * -1);
            teams.get(team).setOffensiveRating(prediction.getTeam1Score());
            teams.get(team).setDefensiveRating(prediction.getTeam2Score());
        }
    }

    private void calculateOtherTeamRankings() {
        for (String team : teams.keySet()) {
            double top25Rating = 0.0;
            double count = 0.0;
            for (int i = 20; i < 25; i++) {
                if (!team.equals(rankedTeams.get(i).getName())) {
                    Prediction prediction = predictGame(team, rankedTeams.get(i).getName(), Location.NEUTRAL);
                    top25Rating += prediction.getLine() * -1;
                    count++;
                }
            }
            teams.get(team).setTop25Rating(top25Rating / count);
            double top5Rating = 0.0;
            count = 0.0;
            for (int i = 0; i < 5; i++) {
                if (!team.equals(rankedTeams.get(i).getName())) {
                    Prediction prediction = predictGame(team, rankedTeams.get(i).getName(), Location.NEUTRAL);
                    top5Rating += prediction.getLine() * -1;
                    count++;
                }
            }
            teams.get(team).setTop5Rating(top5Rating / count);
            teams.get(team).setHomeRating(predictVersusAverage(team, Location.HOME).getLine() * -1);
            teams.get(team).setAwayRating(predictVersusAverage(team, Location.AWAY).getLine() * -1);
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
                double recencyModifier = Math.pow(statRecencyBias, (this.year - year) * (this.teams.get(team1).getNumberOfGames() == 0 ? 1 : this.teams.get(team1).getNumberOfGames()));
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
            double recencyModifier = Math.pow(serRecencyBias, (ser.length - i - 1) * (this.teams.get(team1).getNumberOfGames() == 0 ? 1 : this.teams.get(team1).getNumberOfGames()));
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
