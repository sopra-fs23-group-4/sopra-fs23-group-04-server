package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class RoundEndDTO {
    public static final String type = "roundEnd";
    private int round;
    public String getType(){
        return type;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}