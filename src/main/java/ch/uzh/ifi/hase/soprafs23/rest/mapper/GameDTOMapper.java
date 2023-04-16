package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface GameDTOMapper {

    GameDTOMapper INSTANCE = Mappers.getMapper(GameDTOMapper.class);

    @Mapping(source = "hostId", target = "hostId")
    @Mapping(source = "rounds", target = "rounds")
    @Mapping(source = "roundLength", target = "roundLength")
    @Mapping(source = "categories", target = "categories", qualifiedByName = "mapCategories")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    @Named("mapCategories")
    default List<Category> mapCategories(List<String> categories) {
        List<Category> mappedCategories = new ArrayList<>();
        for (String category : categories) {
            Category mappedCategory = new Category();
            mappedCategory.setName(category);
            mappedCategories.add(mappedCategory);
        }
        return mappedCategories;
    }
}
