package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class VotingTimerDTO {
    public static String type="votingTimer";

    public static String getType() {
        return type;
    }

    private int timeRemaining;

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}
