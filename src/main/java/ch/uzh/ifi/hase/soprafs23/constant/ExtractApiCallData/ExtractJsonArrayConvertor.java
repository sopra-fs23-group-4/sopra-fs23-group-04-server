package ch.uzh.ifi.hase.soprafs23.constant.ExtractApiCallData;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.FactHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;

public class ExtractJsonArrayConvertor implements ExtractJsonData {
    @Override
    public QuoteHolder jsonToQuoteHolder(JsonNode jsonResponse, QuoteCategory quoteCategory) {

        QuoteHolder quoteHolder = new QuoteHolder();
        if (quoteCategory.fieldName.equals("quote")){
            quoteHolder.setQuote(jsonResponse.get(0).get(quoteCategory.fieldName).asText()+ " -"+ jsonResponse.get(0).get("author").asText());
        }
        else {
        quoteHolder.setQuote(jsonResponse.get(0).get(quoteCategory.fieldName).asText());}
        quoteHolder.setCategory(quoteCategory.fieldName);
        return quoteHolder;

    }
}
