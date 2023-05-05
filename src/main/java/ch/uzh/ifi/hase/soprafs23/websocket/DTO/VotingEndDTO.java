package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class VotingEndDTO {
    private final static String type="votingEnd";

    public static String getType() {
        return type;
    }
    private String filler;

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }
}
