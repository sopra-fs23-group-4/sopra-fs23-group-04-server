package ch.uzh.ifi.hase.soprafs23.constant.extractJsonData;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;

public interface ExtractJsonData {
    public QuoteHolder jsonToQuoteHolder(JsonNode jsonResponse, QuoteCategory quoteCategory);
}
