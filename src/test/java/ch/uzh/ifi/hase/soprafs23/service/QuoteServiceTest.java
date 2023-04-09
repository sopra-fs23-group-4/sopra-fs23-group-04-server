package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ExtractJsonArrayConvertor;
import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class QuoteServiceTest {

    private QuoteService quoteService;
    private QuoteApiCaller quoteApiCaller;
    private  QuoteCategory quoteCategory;

    @BeforeEach
    public void setUp() {
        quoteApiCaller = Mockito.mock(QuoteApiCaller.class);
        quoteService = new QuoteService(quoteApiCaller);
        quoteCategory = QuoteCategory.DADJOKE;
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
    public void testInvalidCategory() {
        // Mock the API call to return null
        when(quoteApiCaller.callApi(Mockito.any(QuoteCategory.class), Mockito.anyString())).thenReturn(null);

        // Test assertions here
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            quoteService.generateQuote(quoteCategory);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getReason().contains(quoteCategory.categoryName));
    }

    /*
    @Test
    public void testAllValidCategories() throws NoSuchFieldException, IllegalAccessException {
        // Prepare a sample JSON response for each QuoteCategory
        String sampleJsonResponseArray = "[{\"joke\": \"Sample joke\"}]";
        String sampleJsonResponseElement = "{\"joke\": \"Sample joke\"}";

        // Retrieve the apiKey from the QuoteService class using Reflection
        Field apiKeyField = QuoteService.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        String apiKey = (String) apiKeyField.get(quoteService);

        // Mock the callApi method for QuoteApiCaller
        for (QuoteCategory quoteCategory : QuoteCategory.values())  {
            if (quoteCategory.extractJsonData instanceof ExtractJsonArrayConvertor) {
                when(quoteApiCaller.callApi(quoteCategory, apiKey)).thenReturn(sampleJsonResponseArray);
            } else {
                when(quoteApiCaller.callApi(quoteCategory, apiKey)).thenReturn(sampleJsonResponseElement);
            }
        }

        // Test generateQuote method for all valid categories
        for (QuoteCategory quoteCategory : QuoteCategory.values()) {
            QuoteHolder quoteHolder = quoteService.generateQuote(quoteCategory);
            System.out.println("Testing QuoteCategory: " + quoteCategory);
            assertNotNull(quoteHolder);
            assertNotNull(quoteHolder.getQuote());
        }

    }
     */

}