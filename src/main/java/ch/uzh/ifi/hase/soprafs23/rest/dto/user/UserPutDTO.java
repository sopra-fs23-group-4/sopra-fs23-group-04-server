package ch.uzh.ifi.hase.soprafs23.rest.dto.user;

public class UserPutDTO {

    private int userId;
    private String token;
    private String username;
    private String password;
    private String quote;
    private String profilePictureUrl;


    public UserPutDTO() {
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
