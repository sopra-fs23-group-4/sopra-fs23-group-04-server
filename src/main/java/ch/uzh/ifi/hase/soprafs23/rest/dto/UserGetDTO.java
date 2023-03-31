package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import java.time.LocalDate;
import java.util.Date;

public class UserGetDTO {

  private Long id;
  private String username;
  private UserStatus status;
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

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

  public LocalDate getCreationDate() { return creationDate; }

}
