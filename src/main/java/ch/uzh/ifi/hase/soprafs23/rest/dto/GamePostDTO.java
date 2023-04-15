package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.List;

public class GamePostDTO {
    private Long hostId;
    private int rounds;
    private List<String> categories;

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
