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
public class CategoryRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void findByName_success() {
        // given
        Category category = new Category();
        category.setName("TestCategory");

        entityManager.persist(category);
        entityManager.flush();

        // when
        Optional<Category> found = categoryRepository.findByName(category.getName());

        // then
        assertTrue(found.isPresent());
        assertEquals(found.get().getName(), category.getName());
    }

    @Test
    public void findByName_failure() {
        // given
        String categoryName = "NonExistentCategory";

        // when
        Optional<Category> found = categoryRepository.findByName(categoryName);

        // then
        assertFalse(found.isPresent());
    }
}