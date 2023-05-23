package ch.uzh.ifi.hase.soprafs23.rest.dto.rejoin;

public class RejoinPossibleDTO {
    private boolean rejoinPossible;
    private int gamePin;

    public int getGamePin() {
        return gamePin;
    }

    public void setGamePin(int gamePin) {
        this.gamePin = gamePin;
    }

    public boolean isRejoinPossible() {
        return rejoinPossible;
    }

    public void setRejoinPossible(boolean rejoinPossible) {
        this.rejoinPossible = rejoinPossible;
    }
}
