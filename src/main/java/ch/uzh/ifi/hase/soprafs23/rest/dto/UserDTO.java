package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.time.LocalDate;

public class UserDTO {

    private Long id;
    private String username;
    private String status;
    private String token;
    private LocalDate creationDate;



    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
    return id;
  }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
    return username;
  }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
    return status;
  }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {  return token; }

    public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

    public LocalDate getCreationDate() {
      return creationDate;
    }


}
