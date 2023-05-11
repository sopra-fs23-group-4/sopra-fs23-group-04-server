package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class VotingEndDTO {
    private static final String TYPE = "votingEnd";

    public static String getType() {
        return TYPE;
    }
    private String filler;

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }
}
