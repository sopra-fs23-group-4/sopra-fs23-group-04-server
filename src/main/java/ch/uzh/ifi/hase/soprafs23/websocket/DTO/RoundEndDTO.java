package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class RoundEndDTO {
    public static final String type="roundEnd";
    private String rounded;
    public String getType(){
        return type;
    }

    public String getRounded() {
        return rounded;
    }

    public void setRounded(String rounded) {
        this.rounded = rounded;
    }
}