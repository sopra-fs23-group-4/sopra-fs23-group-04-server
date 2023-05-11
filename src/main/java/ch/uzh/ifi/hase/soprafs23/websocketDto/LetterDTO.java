package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class LetterDTO {

    public static final String TYPE = "letter";

    public String getType(){
        return TYPE;
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
