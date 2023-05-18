package ch.uzh.ifi.hase.soprafs23.rest.mapper;


import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteCategoriesGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
class DTOMapperTest {
    @Test
    void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("password");
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);


    assertEquals(userPostDTO.getUsername(), user.getUsername());
    assertEquals(user.getPassword(),userPostDTO.getPassword());
    }

    @Test
    void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();

        user.setUsername("firstname@lastname");
        user.setToken("1");

        user.setQuote("My penis was in the Guinness book of records!" +
                    "\n" +
                    "Until the librarian told me to take it out.");
        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());

        assertEquals(user.getUsername(), userGetDTO.getUsername());
    }


    @Test
    void testCreateUser_fromUserPutDTOToEntity_success() {
        // create UserPutDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setQuote("quote");


        // MAP -> Convert to user
        User user = UserDTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

        // check content
        assertEquals(userPutDTO.getQuote(), user.getQuote());

    }
    @Test
    void testCreateUser_fromLoginPostDTO_success() {
        // create UserLoginDTO
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("username");
        userLoginDTO.setPassword("password");

        // MAP -> Convert to user
        User user = UserDTOMapper.INSTANCE.convertUserLoginPostDTOtoEntity(userLoginDTO);

        // check content
        assertEquals(userLoginDTO.getUsername(), user.getUsername());
        assertEquals(userLoginDTO.getPassword(), user.getPassword());
    }

    @Test
    void testCreateUser_fromLogoutDTO_success() {
        // create UserLogoutDTO
        UserLogoutDTO userLogoutDTO = new UserLogoutDTO();
        userLogoutDTO.setToken("token");

        // MAP -> Convert to user
        User user = UserDTOMapper.INSTANCE.convertUserLogoutDTOtoEntity(userLogoutDTO);

        // check content
        assertEquals(userLogoutDTO.getToken(), user.getToken());
    }

    @Test
    void testCreateGetUserDTO_fromUser_success() {
        // create User
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setCreationDate(LocalDate.EPOCH);


        // MAP -> Convert to UserGetDTO
        UserGetDTO userGetDTO = UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());

    }
    @Test
    void testCreateQuoteGetDTO_fromQuoteHolder_success() {

        QuoteHolder quoteHolder = new QuoteHolder();

        quoteHolder.setQuote("quote");

        QuoteGetDTO quoteGetDTO = UserDTOMapper.INSTANCE.convertEntityToQuoteGetDTO(quoteHolder);

        assertEquals(quoteHolder.getQuote(), quoteGetDTO.getQuote());
    }

    @Test
    void testConvertEntityToQuoteCategoriesGetDTO_success() {

        QuoteCategoriesHolder quoteCategory = new QuoteCategoriesHolder();
        List<String> categories = new ArrayList<>();
        categories.add("category1");
        categories.add("category2");
        quoteCategory.setCategories(categories);

        QuoteCategoriesGetDTO quoteCategoriesGetDTO = UserDTOMapper.INSTANCE.convertEntityToQuoteCategoriesGetDTO(quoteCategory);

        assertEquals(quoteCategory.getCategories(), quoteCategoriesGetDTO.getCategories());
    }


}
