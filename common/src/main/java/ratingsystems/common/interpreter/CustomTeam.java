package ratingsystems.common.interpreter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public abstract class CustomTeam {
    protected Team team;

    /**
     * Returns the name of the team
     *
     * @return the name of the team
     */
    public String getName() {
        return team.getName();
    }

    /**
     * Returns the conference the team belongs to
     *
     * @return the conference the team belongs to
     */
    public String getConference() {
        return team.getConference();
    }

    public String getCoach() {
        return team.getCoach();
    }

    public int getYear() {
        return team.getYear();
    }

    /**
     * Sets the rating of the team
     *
     * @param rating the new rating of the team
     */
    public void setRating(double rating) {
        team.setRating(rating);
    }

    /**
     * Returns the rating of the team
     *
     * @return the rating of the team
     */
    public double getRating() {
        return team.getRating();
    }

    /**
     * Returns the number of games the team played
     *
     * @return the number of games the team played
     */
    public int getNumberOfGames() {
        return team.getNumberOfGames();
    }

    /**
     * Returns a copy of the games of the team
     *
     * @return a copy of the games of the team
     */
    @JsonIgnore
    public List<Game> getGames() {
        return team.getGames();
    }

    public int getNumberOfWins() {
        return team.getNumberOfWins();
    }

    public int getNumberOfLosses() {
        return team.getNumberOfLosses();
    }

    public String getRecord() {
        return team.getRecord();
    }
}
