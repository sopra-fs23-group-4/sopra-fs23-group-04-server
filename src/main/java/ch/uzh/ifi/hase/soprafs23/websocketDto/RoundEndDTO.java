package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class RoundEndDTO {
    public static final String TYPE = "roundEnd";
    private String rounded;
    public String getType(){
        return TYPE;
    }

    public String getRounded() {
        return rounded;
    }

    public void setRounded(String rounded) {
        this.rounded = rounded;
    }
}