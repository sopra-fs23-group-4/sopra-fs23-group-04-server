package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class UserPutDTO {
    private String username;
    private String quote;
    private Byte[] picture;

    public String getUsername() {
        return username;
    }

    public String getQuote() {
        return quote;
    }

    public Byte[] getPicture() {
        return picture;
    }
}
