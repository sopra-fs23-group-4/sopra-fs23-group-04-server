package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.exceptions.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.exceptions.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.exceptions.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteCategoriesGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteGetDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserLoginPostDTOtoEntity(UserLoginDTO userLoginDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "creationDate",target = "creationDate")
    @Mapping(source = "profilePictureUrl", target = "profilePictureUrl")
    UserGetDTO convertEntityToUserGetDTO(User user);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "creationDate",target = "creationDate")
    @Mapping(source = "token", target = "token")
    UserDTO convertEntityToUserDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "quote", target = "quote")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "profilePictureUrl",target = "profilePictureUrl")
    User convertUserPutDTOToEntity(UserPutDTO userPutDTO);

    @Mapping(source = "quote", target = "quote")
    QuoteGetDTO convertEntityToQuoteGetDTO(QuoteHolder quoteHolder);

    @Mapping(source ="categories", target = "categories")
    QuoteCategoriesGetDTO convertEntityToQuoteCategoriesGetDTO(QuoteCategoriesHolder quoteCategory);

}
