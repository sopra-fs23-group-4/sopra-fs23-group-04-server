package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class RoundTimerDTO {
    public static final String type = "roundTimer";

    private int timeRemaining;
    public String getType(){
        return type;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}