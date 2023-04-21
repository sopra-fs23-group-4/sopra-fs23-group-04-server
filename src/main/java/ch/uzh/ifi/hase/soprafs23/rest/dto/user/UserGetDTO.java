package ch.uzh.ifi.hase.soprafs23.rest.dto.user;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import java.time.LocalDate;
import java.util.Date;

public class UserGetDTO {

    private Long id;
    private String username;
    private UserStatus status;
    private LocalDate creationDate;
    private String quote;
    private String profilePictureUrl;

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public UserStatus getStatus() { return status; }

    public void setStatus(UserStatus status) { this.status = status; }

    public LocalDate getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public String getProfilePictureUrl() { return profilePictureUrl; }

    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

}
