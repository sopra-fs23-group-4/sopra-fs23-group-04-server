package ch.uzh.ifi.hase.soprafs23.entity.game;

import ch.uzh.ifi.hase.soprafs23.constant.VoteOption;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "VOTE")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private int voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private VoteOption votedOption;

    public int getVoteId() {
        return voteId;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VoteOption getVotedOption() {
        return votedOption;
    }

    public void setVotedOption(VoteOption votedOption) {
        this.votedOption = votedOption;
    }

}
