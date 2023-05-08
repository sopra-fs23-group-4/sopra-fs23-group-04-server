package ch.uzh.ifi.hase.soprafs23.constant.ExtractApiCallData;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;

public interface ExtractJsonData {
    QuoteHolder jsonToQuoteHolder(JsonNode jsonResponse, QuoteCategory quoteCategory);
}
