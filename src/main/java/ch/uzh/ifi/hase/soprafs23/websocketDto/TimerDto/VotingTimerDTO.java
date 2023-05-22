package ch.uzh.ifi.hase.soprafs23.websocketDto.TimerDto;

public class VotingTimerDTO {
    public static final String TYPE = "votingTimer";

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
