package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.time.LocalDate;

public class UserDTO {

    private Long id;
    private String username;
    private String status;
    private String token;
    private LocalDate creationDate;
    private String quote;
    private String profilePictureUrl;
    private int accumulatedScore;




    public Long getId() {
    return id;
  }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
    return username;
  }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
    return status;
  }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {  return token; }

    public void setToken(String token) { this.token = token; }

    public LocalDate getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public String getQuote() { return quote; }

    public void setQuote(String quote) { this.quote = quote; }

    public String getProfilePictureUrl() { return profilePictureUrl; }

    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public int getAccumulatedScore() { return accumulatedScore; }

    public void setAccumulatedScore(int accumulatedScore) { this.accumulatedScore = accumulatedScore; }

}
