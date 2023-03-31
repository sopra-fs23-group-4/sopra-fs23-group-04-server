package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
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
  @Mapping(source = "creation_date",target = "creation_date")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "creation_date",target = "creation_date")
  @Mapping(source = "token", target = "token")
  UserDTO convertEntityToUserDTO(User user);

  @Mapping(source = "quote", target = "quote")
  QuoteGetDTO convertEntityToQuoteGetDTO(QuoteHolder quoteHolder);

}
