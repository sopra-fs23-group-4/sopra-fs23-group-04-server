package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class UserJoinDTO {
    public final static String type="userJoin";

    public String getType(){
        return type;
    }
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
