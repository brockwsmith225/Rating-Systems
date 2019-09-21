package ratingsystems.hps;

import ratingsystems.common.interpreter.Game;
import ratingsystems.common.interpreter.Interpreter;
import ratingsystems.common.interpreter.Team;
import ratingsystems.common.ratingsystem.Prediction;
import ratingsystems.common.ratingsystem.RatingSystem;
import ratingsystems.common.linalg.Vector;
import ratingsystems.ser.SimpleEfficiencyRating;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoricalPredictionSystem extends RatingSystem {

    private HashMap<Integer, HashMap<String, Vector>> teamVectors;
    private HashMap<Integer, HashMap<String, Vector>> scaledTeamVectors;
    private HashMap<Integer, HashMap<String, Team>> allTeams;
    private SimpleEfficiencyRating[] ser;
    private boolean hasRankedTeams, hasRankedGroups;

    public HistoricalPredictionSystem(Interpreter interpreter, int year) throws FileNotFoundException {
        super(interpreter, year);
        hasRankedTeams = false;
        hasRankedGroups = false;
        teamVectors = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        teamVectors.put(year, new HashMap<>());
        for (String team : teams.keySet()) {
            allTeams.get(year).put(team, teams.get(team));
            teamVectors.get(year).put(team, teams.get(team).getStatisticsVector());
        }
        for (int y = 2012; y < year; y++) {
            if (interpreter.hasData(y)) {
                HashMap<String, Team> temp = interpreter.parseData(y);
                allTeams.put(y, new HashMap<>());
                teamVectors.put(y, new HashMap<>());
                for (String team : temp.keySet()) {
                    allTeams.get(y).put(team, temp.get(team));
                    teamVectors.get(y).put(team, temp.get(team).getStatisticsVector());
                }
            }
        }
        week = getWeek();
    }

    public HistoricalPredictionSystem(Interpreter interpreter, int year, int week) throws FileNotFoundException {
        super(interpreter, year, week);
        hasRankedTeams = false;
        hasRankedGroups = false;
        teamVectors = new HashMap<>();
        allTeams = new HashMap<>();
        allTeams.put(year, new HashMap<>());
        teamVectors.put(year, new HashMap<>());
        for (String team : teams.keySet()) {
            allTeams.get(year).put(team, teams.get(team));
            teamVectors.get(year).put(team, teams.get(team).getStatisticsVector());
        }
        for (int y = 2012; y < year; y++) {
            if (interpreter.hasData(y)) {
                HashMap<String, Team> temp = interpreter.parseData(y);
                allTeams.put(y, new HashMap<>());
                teamVectors.put(y, new HashMap<>());
                for (String team : temp.keySet()) {
                    allTeams.get(y).put(team, temp.get(team));
                    teamVectors.get(y).put(team, temp.get(team).getStatisticsVector());
                }
            }
        }
    }

    @Override
    public void setup() {
        scaledTeamVectors = new HashMap<>();
        for (Integer year : allTeams.keySet()) {
            scaledTeamVectors.put(year, new HashMap<>());
            for (String team : allTeams.get(year).keySet()) {
                scaledTeamVectors.get(year).put(team, allTeams.get(year).get(team).getStatisticsVector(teamVectors.get(year)));
            }
        }

        int prevYears = 4;
        try {
            ser = new SimpleEfficiencyRating[prevYears];
            if (this.week < 0) {
                for (int i = 0; i < prevYears; i++) {
                    if (week == 0) {
                        this.ser[i] = new SimpleEfficiencyRating(this.interpreter, this.year - (prevYears - i));
                    } else {
                        this.ser[i] = new SimpleEfficiencyRating(this.interpreter, this.year - (prevYears - 1 - i));
                    }
                    this.ser[i].setup();
                }
            } else {
                for (int i = 0; i < prevYears - 1; i++) {
                    if (week == 0 || week == 1) {
                        this.ser[i] = new SimpleEfficiencyRating(this.interpreter, this.year - (prevYears - i), this.week);
                    } else {
                        this.ser[i] = new SimpleEfficiencyRating(this.interpreter, this.year - (prevYears - 1 - i), this.week);
                    }
                    this.ser[i].setup();
                }
                if (week == 0 || week == 1) {
                    this.ser[prevYears - 1] = new SimpleEfficiencyRating(this.interpreter, this.year - 1);
                } else {
                    this.ser[prevYears - 1] = new SimpleEfficiencyRating(this.interpreter, this.year, this.week);
                }
                this.ser[prevYears - 1].setup();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rankTeams() {
        hasRankedTeams = true;
        HashMap<String, Double> ratings = new HashMap<>();
        for (String team : allTeams.get(this.year).keySet()) {
            ratings.put(team, 0.0);
            for (String opponent : allTeams.get(this.year).keySet()) {
                if (!team.equals(opponent)) {
                    ratings.put(team, ratings.get(team) + predictGame(team, opponent).getLine() * -1);
                }
            }
        }

        for (String team : allTeams.get(year).keySet()) {
            allTeams.get(this.year).get(team).setRating(ratings.get(team) / (ratings.size() - 1));
        }
        super.rankTeams();
    }

    @Override
    public void rankGroups() {

    }

    @Override
    public String printTeamRankings(boolean prettyPrint) {
        if (!hasRankedTeams) {
            rankTeams();
        }
        return super.printTeamRankings(prettyPrint);
    }

    @Override
    public Prediction predictGame(String team1, String team2) {
        HashMap<String, Double> team1Similarities = new HashMap<>();
        HashMap<String, Double> team2Similarities = new HashMap<>();

        Vector team1Vector = new Vector(28, 0.0);
        Vector team2Vector = new Vector(28, 0.0);
        double team1ModifiedCount = 0.0;
        double team2ModifiedCount = 0.0;
        double recencyBias = 0.9;
        for (int i = 0; i < 2; i++) {
            int year;
            if (week == 0 || week == 1) {
                year = this.year - i - 1;
            } else {
                year = this.year - i;
            }
            double recencyModifier = Math.pow(recencyBias, (this.year - year) * this.week);
            if (scaledTeamVectors.get(year).containsKey(team1)) {
                team1Vector = team1Vector.add(scaledTeamVectors.get(year).get(team1).multiply(recencyModifier));
                team1ModifiedCount += recencyModifier;
            }
            if (scaledTeamVectors.get(year).containsKey(team2)) {
                team2Vector = team2Vector.add(scaledTeamVectors.get(year).get(team2).multiply(recencyModifier));
                team2ModifiedCount += recencyModifier;
            }
        }

        team1Vector = team1Vector.multiply(1.0 / team1ModifiedCount);
        team2Vector = team2Vector.multiply(1.0 / team2ModifiedCount);

        if (team1Vector.magnitude() == 0.0) {
            team1Vector = new Vector(team1Vector.size(), 1.0);
        }
        if (team2Vector.magnitude() == 0.0) {
            team2Vector = new Vector(team2Vector.size(), 1.0);
        }

        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    team1Similarities.put(historicalTeam, team1Vector.cosineSimilarity(scaledTeamVectors.get(year).get(historicalTeam)));
                    team2Similarities.put(historicalTeam, team2Vector.cosineSimilarity(scaledTeamVectors.get(year).get(historicalTeam)));
                }
            }
        }

        ArrayList<Double> gameSimilarities = new ArrayList<>();
        ArrayList<Double> team1Scores = new ArrayList<>();
        ArrayList<Double> team2Scores = new ArrayList<>();
        for (Integer year : allTeams.keySet()) {
            if (year != this.year) {
                for (String historicalTeam : allTeams.get(year).keySet()) {
                    for (Game game : allTeams.get(year).get(historicalTeam).getGames()) {
                        double gameSimilarity = team1Similarities.get(historicalTeam) * team2Similarities.get(game.getOpponent());
                        gameSimilarities.add(gameSimilarity);
                        team1Scores.add(game.getScore());
                        team2Scores.add(game.getOpponentScore());
                    }
                }
            }
        }

        Vector gameSimilaritiesVector = new Vector(gameSimilarities).softmax();
        Vector team1ScoresVector = new Vector(team1Scores);
        Vector team2ScoresVector = new Vector(team2Scores);

        double team1Off = 0.0;
        double team1Def = 0.0;
        double team2Off = 0.0;
        double team2Def = 0.0;
        team1ModifiedCount = 0.0;
        team2ModifiedCount = 0.0;
        recencyBias = 0.9;
        for (int i = 0; i < ser.length; i++) {
            double recencyModifier = Math.pow(recencyBias, (ser.length - i - 1) * this.week);
            if (ser[i].hasTeam(team1)) {
                team1Off += ser[i].getTeam(team1).getRating("Offensive Rating") * recencyModifier;
                team1Def += ser[i].getTeam(team1).getRating("Defensive Rating") * recencyModifier;
                team1ModifiedCount += recencyModifier;
            }
            if (ser[i].hasTeam(team2)) {
                team2Off += ser[i].getTeam(team2).getRating("Offensive Rating") * recencyModifier;
                team2Def += ser[i].getTeam(team2).getRating("Defensive Rating") * recencyModifier;
                team2ModifiedCount += recencyModifier;
            }
        }

        team1Off /= team1ModifiedCount;
        team1Def /= team1ModifiedCount;
        team2Off /= team2ModifiedCount;
        team2Def /= team2ModifiedCount;

        if (team1Off == 0.0) team1Off = 1.0;
        if (team1Def == 0.0) team1Def = 1.0;
        if (team2Off == 0.0) team2Off = 1.0;
        if (team2Def == 0.0) team2Def = 1.0;

        double team1ExpectedScore = gameSimilaritiesVector.dotProduct(team1ScoresVector) * team1Off / team2Def;
        double team2ExpectedScore = gameSimilaritiesVector.dotProduct(team2ScoresVector) * team2Off / team1Def;

        double odds = team1ExpectedScore / (team1ExpectedScore + team2ExpectedScore);

        return new Prediction(team1, team2, odds, team1ExpectedScore, team2ExpectedScore);
    }

}
