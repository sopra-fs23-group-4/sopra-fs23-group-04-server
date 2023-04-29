package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class RoundTimerDTO {
    public static final String type="roundTimer";

    private int timeRemaining;

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}