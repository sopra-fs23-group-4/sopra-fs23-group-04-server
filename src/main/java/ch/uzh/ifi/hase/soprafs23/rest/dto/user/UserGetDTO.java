package ch.uzh.ifi.hase.soprafs23.rest.dto.user;


import java.time.LocalDate;

public class UserGetDTO {

    private int id;
    private String username;
    private LocalDate creationDate;
    private String quote;


    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public LocalDate getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

}
