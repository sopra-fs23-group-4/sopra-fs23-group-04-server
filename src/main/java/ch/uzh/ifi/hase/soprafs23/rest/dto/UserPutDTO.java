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

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getQuote() {
        return quote;
    }

    public Byte[] getPicture() {
        return profile_picture;
    }
}
