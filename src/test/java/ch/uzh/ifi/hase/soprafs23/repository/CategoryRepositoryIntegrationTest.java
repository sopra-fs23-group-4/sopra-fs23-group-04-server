package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.repository.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CategoryRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByName_success() {
        // given
        Category category = new Category();
        category.setName("TestCategory");

        entityManager.persist(category);
        entityManager.flush();

        // when
        Optional<Category> found = Optional.ofNullable(categoryRepository.findByName(category.getName()));

        // then
        assertTrue(found.isPresent());
        assertEquals(found.get().getName(), category.getName());
    }

    @Test
    void findByName_failure() {
        // given
        String categoryName = "NonExistentCategory";

        // when
        Optional<Category> found = Optional.ofNullable(categoryRepository.findByName(categoryName));

        // then
        assertFalse(found.isPresent());
    }
}