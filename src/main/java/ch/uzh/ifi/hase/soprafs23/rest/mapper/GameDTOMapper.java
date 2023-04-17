package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.service.CategoryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.hase.soprafs23.service.CategoryService.categoryRepository;

@Mapper
public interface GameDTOMapper {

    GameDTOMapper INSTANCE = Mappers.getMapper(GameDTOMapper.class);

    @Mapping(source = "hostId", target = "hostId")
    @Mapping(source = "rounds", target = "rounds")
    @Mapping(source = "roundLength", target = "roundLength")
    @Mapping(source = "categories", target = "categories", qualifiedByName = "mapCategories")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    /** transform the category strings to category objects */
    @Named("mapCategories")
    default List<Category> mapCategories(List<String> categories) {
        List<Category> mappedCategories = new ArrayList<>();
        for (String category : categories) {
            Category mappedCategory = categoryRepository.findByName(category).orElse(null);
            if (mappedCategory == null) {
                mappedCategory = new Category();
                mappedCategory.setName(category);
            }
            mappedCategories.add(mappedCategory);
        }
        return mappedCategories;
    }
}

        //System.out.println("\n-----------------------------------------");
        //System.out.println(ex);
        //System.out.println(request);
        //System.out.println("-----------------------------------------\n");