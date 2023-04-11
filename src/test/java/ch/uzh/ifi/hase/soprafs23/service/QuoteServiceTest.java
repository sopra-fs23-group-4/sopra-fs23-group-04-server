package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteServiceTest {
    private QuoteService quoteService;

    @BeforeEach
    public void setUp() {
        quoteService = Mockito.spy(new QuoteService());
    }

    @Test
    public void testGetAllCategories() {
        // Call the getCategories method
        QuoteCategoriesHolder quoteCategoriesHolder = quoteService.getCategories();

        // Assert that the result is not null
        assertNotNull(quoteCategoriesHolder);

        // Assert that the categories list is not null
        assertNotNull(quoteCategoriesHolder.getCategories());

        List<String> expectedCategories = QuoteCategory.getAllCategoryNames();

        // Assert that the actual categories are equal to the expected categories
        assertEquals(expectedCategories, quoteCategoriesHolder.getCategories());
    }

    @Test
    public void testInvalidCategory(){
        String invalidCategory = "invalidCategory";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            quoteService.generateQuote(invalidCategory);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testAllValidCategories(){
        for (QuoteCategory quoteCategory: QuoteCategory.values()){
            QuoteHolder quoteHolder= quoteService.generateQuote(quoteCategory.categoryName);
            assertNotNull(quoteHolder);
            assertNotNull(quoteHolder.getQuote());
        }
    }



}