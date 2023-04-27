package ch.uzh.ifi.hase.soprafs23.entity.game;

import ch.uzh.ifi.hase.soprafs23.constant.ScorePoint;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "ANSWER")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String answerString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @Column(nullable = false)
    private ScorePoint scorePoint;

    public Long getAnswerId() {
        return id;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAnswerString() {
        return answerString;
    }

    public void setAnswerString(String answerString) {
        this.answerString = answerString;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ScorePoint getScorePoint() {
        return scorePoint;
    }

    public void setScorePoint(ScorePoint scorePoint) {
        this.scorePoint = scorePoint;
    }
}
