package ch.uzh.ifi.hase.soprafs23.constant;

public enum RoundLength {
    SHORT(30),
    MEDIUM(60),
    LONG(90);

    private final int duration;

    RoundLength(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}