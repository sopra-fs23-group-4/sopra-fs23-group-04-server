package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class UserPutDTO {
    private String token;
    private Long userId;
    private String username;
    private String password;
    private String quote;
    private Byte[] profile_picture;

    public UserPutDTO() {
    }

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

    public Byte[] getPicture() {
        return profile_picture;
    }
}
