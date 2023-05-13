package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class ResultTimerDTO {
    public static final String TYPE = "resultTimer";

    private int timeRemaining;
    public String getType(){
        return TYPE;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}
