package ch.uzh.ifi.hase.soprafs23.constant.extractJsonData;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;

public class ExtractJsonArrayConvertor implements ExtractJsonData {
    @Override
    public QuoteHolder jsonToQuoteHolder(JsonNode jsonResponse, QuoteCategory quoteCategory) {

        QuoteHolder quoteHolder = new QuoteHolder();
        quoteHolder.setQuote(jsonResponse.get(0).get(quoteCategory.fieldName).asText());
        quoteHolder.setCategory(quoteCategory.fieldName);
        return quoteHolder;
    }
}
