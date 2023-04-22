package ch.uzh.ifi.hase.soprafs23.constant;

import ch.uzh.ifi.hase.soprafs23.constant.extractJson.ExtractJsonArrayConvertor;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtractJsonArrayConvertorTest {
    ExtractJsonArrayConvertor extractJsonArrayConvertor = new ExtractJsonArrayConvertor();
    @Test
    void testJsonToQuoteHolder() throws Exception {
        // Set up input data
        String jsonString = "[{\"joke\": \"When putting cheese in a mousetrap, always leave room for the mouse.\"}]";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(jsonString);

        QuoteCategory quoteCategory=QuoteCategory.DADJOKE;

        // Call the jsonToQuoteHolder function with the input data
        QuoteHolder result = extractJsonArrayConvertor.jsonToQuoteHolder(jsonResponse, quoteCategory);

        // Assert the expected output
        assertEquals("When putting cheese in a mousetrap, always leave room for the mouse.", result.getQuote());
        assertEquals("joke", result.getCategory());
    }

}