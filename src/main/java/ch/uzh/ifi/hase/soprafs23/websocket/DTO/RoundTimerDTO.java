package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class RoundTimerDTO {
    public  final static String type="roundTimer";

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