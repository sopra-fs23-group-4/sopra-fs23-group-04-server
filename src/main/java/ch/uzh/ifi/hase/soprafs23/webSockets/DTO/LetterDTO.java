package ch.uzh.ifi.hase.soprafs23.webSockets.DTO;

public class LetterDTO {
    private Character letter;

    private boolean isLastRound;

    public Character getLetter() {
        return letter;
    }

    public void setLetter(Character letter) { this.letter = letter; }

    public boolean getIsLastRound() { return isLastRound; }

    public void setIsLastRound(boolean isLastRound) { this.isLastRound = isLastRound; }

}
