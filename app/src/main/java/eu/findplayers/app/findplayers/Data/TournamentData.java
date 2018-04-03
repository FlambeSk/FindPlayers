package eu.findplayers.app.findplayers.Data;

/**
 * Created by CWSK_DEV on 3/19/2018.
 */

public class TournamentData {

    private int tournamentID;
    private String tournamnetName,tournamentImage, playersCount, startAt;

    public TournamentData(int tournamentID, String tournamnetName, String tournamentImage, String playersCount, String startAt) {
        this.tournamentID = tournamentID;
        this.tournamnetName = tournamnetName;
        this.tournamentImage = tournamentImage;
        this.playersCount = playersCount;
        this.startAt = startAt;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public String getTournamnetName() {
        return tournamnetName;
    }

    public void setTournamnetName(String tournamnetName) {
        this.tournamnetName = tournamnetName;
    }

    public String getTournamentImage() {
        return tournamentImage;
    }

    public void setTournamentImage(String tournamentImage) {
        this.tournamentImage = tournamentImage;
    }

    public String getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(String playersCount) {
        this.playersCount = playersCount;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }
}
