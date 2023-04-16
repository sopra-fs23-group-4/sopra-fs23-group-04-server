package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteCategoriesGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapping;

import java.nio.charset.StandardCharsets;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("password");
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);


    assertEquals(userPostDTO.getUsername(), user.getUsername());
    assertEquals(user.getPassword(),userPostDTO.getPassword());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();

        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        user.setProfilePictureUrl("https://example.com/test-profile-picture.jpg");
        user.setQuote("My penis was in the Guinness book of records!" +
                    "\n" +
                    "Until the librarian told me to take it out.");
        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());

        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
    }

    @Test
    public void testCreateUser_fromUserPutDTO_toUser_success() {
        // create UserPostDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("username");
        userPutDTO.setPassword("password");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

        // check content
        assertEquals(userPutDTO.getUsername(), user.getUsername());
        assertEquals(userPutDTO.getPassword(), user.getPassword());
    }
    @Test
    public void testCreateUser_fromUserPutDTOToEntity_success() {
        // create UserPutDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("username");
        userPutDTO.setPassword("password");
        userPutDTO.setQuote("quote");
        userPutDTO.setProfilePictureUrl("profilePictureUrl");

        // MAP -> Convert to user
        User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

        // check content
        assertEquals(userPutDTO.getUsername(), user.getUsername());
        assertEquals(userPutDTO.getPassword(), user.getPassword());
        assertEquals(userPutDTO.getQuote(), user.getQuote());
        assertEquals(userPutDTO.getProfilePictureUrl(), user.getProfilePictureUrl());
    }
    @Test
    public void testCreateUser_fromLoginPostDTO_success() {
        // create UserLoginDTO
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("username");
        userLoginDTO.setPassword("password");

        // MAP -> Convert to user
        User user = DTOMapper.INSTANCE.convertUserLoginPostDTOtoEntity(userLoginDTO);

        // check content
        assertEquals(userLoginDTO.getUsername(), user.getUsername());
        assertEquals(userLoginDTO.getPassword(), user.getPassword());
    }

    @Test
    public void testCreateUser_fromLogoutDTO_success() {
        // create UserLogoutDTO
        UserLogoutDTO userLogoutDTO = new UserLogoutDTO();
        userLogoutDTO.setToken("token");

        // MAP -> Convert to user
        User user = DTOMapper.INSTANCE.convertUserLogoutDTOtoEntity(userLogoutDTO);

        // check content
        assertEquals(userLogoutDTO.getToken(), user.getToken());
    }

    @Test
    public void testCreateGetUserDTO_fromUser_success() {
        // create User
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDate.EPOCH);
        user.setProfilePictureUrl("profilePictureUrl");

        // MAP -> Convert to UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
        assertEquals(user.getProfilePictureUrl(), userGetDTO.getProfilePictureUrl());
    }
    @Test
    public void testCreateQuoteGetDTO_fromQuoteHolder_success() {

        QuoteHolder quoteHolder = new QuoteHolder();

        quoteHolder.setQuote("quote");

        QuoteGetDTO quoteGetDTO = DTOMapper.INSTANCE.convertEntityToQuoteGetDTO(quoteHolder);

        assertEquals(quoteHolder.getQuote(), quoteGetDTO.getQuote());
    }

    @Test
    public void testConvertEntityToQuoteCategoriesGetDTO_success() {

        QuoteCategoriesHolder quoteCategory = new QuoteCategoriesHolder();
        List<String> categories = new ArrayList<>();
        categories.add("category1");
        categories.add("category2");
        quoteCategory.setCategories(categories);

        QuoteCategoriesGetDTO quoteCategoriesGetDTO = DTOMapper.INSTANCE.convertEntityToQuoteCategoriesGetDTO(quoteCategory);

        assertEquals(quoteCategory.getCategories(), quoteCategoriesGetDTO.getCategories());
    }


}
