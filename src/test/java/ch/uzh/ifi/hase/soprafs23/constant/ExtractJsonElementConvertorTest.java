package ch.uzh.ifi.hase.soprafs23.constant;


import ch.uzh.ifi.hase.soprafs23.constant.extractJson.ExtractJsonElementConvertor;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtractJsonElementConvertorTest {
    ExtractJsonElementConvertor extractJsonElementConvertor= new ExtractJsonElementConvertor();
    @Test
    void testJsonToQuoteHolder() throws Exception {
        // Set up input data
        String jsonResponseString = "{\"joke\": \"Champions are the breakfast of Chuck Norris.\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(jsonResponseString);

        QuoteCategory quoteCategory=QuoteCategory.CHUCKNORRIS;

        // Call the jsonToQuoteHolder function with the input data
        QuoteHolder result = extractJsonElementConvertor.jsonToQuoteHolder(jsonResponse, quoteCategory);

        // Assert the expected output
        assertEquals("Champions are the breakfast of Chuck Norris.", result.getQuote());
        assertEquals("joke", result.getCategory());
    }
}