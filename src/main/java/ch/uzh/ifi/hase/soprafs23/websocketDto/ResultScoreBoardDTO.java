package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class ResultScoreBoardDTO {
    public static final String type = "resultScoreboard";

    public String getType(){
        return type;
    }
    private int round;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
