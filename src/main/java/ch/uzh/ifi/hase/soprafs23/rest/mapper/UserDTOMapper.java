package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.quote.FactHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.CategoryGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GameSettingGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.FactGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteCategoriesGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.user.*;
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
public interface UserDTOMapper {

    UserDTOMapper INSTANCE = Mappers.getMapper(UserDTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserLoginPostDTOtoEntity(UserLoginDTO userLoginDTO);

    @Mapping(source="token", target="token")
    User convertUserLogoutDTOtoEntity(UserLogoutDTO userLogoutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "creationDate",target = "creationDate")
    @Mapping(source = "quote", target="quote")
    UserGetDTO convertEntityToUserGetDTO(User user);


    @Mapping(source = "quote", target = "quote")
    User convertUserPutDTOToEntity(UserPutDTO userPutDTO);

    @Mapping(source = "quote", target = "quote")
    QuoteGetDTO convertEntityToQuoteGetDTO(QuoteHolder quoteHolder);

    @Mapping(source ="fact", target = "fact")
    FactGetDTO convertEntityToFactGetDTO(FactHolder factHolder);

    @Mapping(source ="categories", target = "categories")
    QuoteCategoriesGetDTO convertEntityToQuoteCategoriesGetDTO(QuoteCategoriesHolder quoteCategory);

    @Mapping(source = "name", target = "categoryName")
    CategoryGetDTO convertEntityToCategoryGetDTO(Category category);

    @Mapping(source = "rounds", target = "rounds")
    @Mapping(source = "roundLength", target = "roundLength")
    @Mapping(source = "status", target = "status")
    GameSettingGetDTO convertEntityToGameSettingGetDTO(Game game);

}
