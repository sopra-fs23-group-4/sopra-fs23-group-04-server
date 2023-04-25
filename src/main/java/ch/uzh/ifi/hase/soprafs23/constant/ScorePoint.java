package ch.uzh.ifi.hase.soprafs23.constant;

public enum ScorePoint {

    CORRECT_UNIQUE(3),
    CORRECT_NOT_UNIQUE(1),
    INCORRECT(0);

    private final int points;

    ScorePoint(int points) { this.points = points; }

    public int getPoints() { return points; }
}
