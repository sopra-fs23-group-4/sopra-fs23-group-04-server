package ch.uzh.ifi.hase.soprafs23.websocket.DTO;

public class LetterDTO {

    public final static String type="letter";

    public String getType(){
        return type;
    }
    private int round;
    private Character letter;

    public int getRound() {
        return round;
    }

    public Character getLetter() {
        return letter;
    }

    public void setLetter(Character letter) {
        this.letter = letter;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
