package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  void findByUsername_success() {
      // given
      User user = new User();

      user.setUsername("firstname@lastname");
      user.setCreationDate(LocalDate.now());
      user.setPassword("abc");
      user.setToken("1");
      user.setQuote("j");


      entityManager.persist(user);
      entityManager.flush();
      User found = userRepository.findByUsername(user.getUsername());
      // when


      // then

      assertEquals(found.getUsername(), user.getUsername());
      assertEquals(found.getToken(), user.getToken());
  }

}
