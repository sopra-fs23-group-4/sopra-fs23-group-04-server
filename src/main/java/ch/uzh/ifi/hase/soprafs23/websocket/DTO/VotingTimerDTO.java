package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class VotingTimerDTO {
    public  final static String type="votingTimer";

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
