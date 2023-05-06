package ch.uzh.ifi.hase.soprafs23.constant;

public enum RoundLength {
    SHORT(15),
    MEDIUM(60),
    LONG(75);

    private final int duration;

    RoundLength(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}