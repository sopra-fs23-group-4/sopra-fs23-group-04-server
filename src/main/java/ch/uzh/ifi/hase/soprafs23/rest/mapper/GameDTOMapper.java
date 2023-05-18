package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.RoundLength;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.GamePostDTO;
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

    @Mapping(source = "rounds", target = "rounds")
    @Mapping(source = "roundLength", target = "roundLength", qualifiedByName = "mapRoundLength")
    @Mapping(source = "categories", target = "categories", qualifiedByName = "mapCategories")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    // transoform the roundLength string to Enums
    @Named("mapRoundLength")
    default RoundLength mapRoundLength(String roundLength) {
        return switch (roundLength) {
            case "SHORT" -> RoundLength.SHORT;
            case "MEDIUM" -> RoundLength.MEDIUM;
            case "LONG" -> RoundLength.LONG;
            default -> throw new IllegalArgumentException("Invalid round length value: " + roundLength);
        };
    }

    /** transform the category strings to category objects */
    @Named("mapCategories")
    default List<Category> mapCategories(List<String> categoryNames) {
        List<Category> mappedCategories = new ArrayList<>();
        for (String categoryName : categoryNames) {
            Category mappedCategory = categoryRepository.findByName(categoryName);
            if (mappedCategory == null) {
                mappedCategory = new Category();
                mappedCategory.setName(categoryName);
            }
            mappedCategories.add(mappedCategory);
        }
        return mappedCategories;
    }
}