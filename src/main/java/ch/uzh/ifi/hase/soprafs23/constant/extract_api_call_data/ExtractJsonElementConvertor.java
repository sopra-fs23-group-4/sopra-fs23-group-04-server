package ch.uzh.ifi.hase.soprafs23.constant.extract_api_call_data;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import com.fasterxml.jackson.databind.JsonNode;

public class ExtractJsonElementConvertor implements ExtractJsonData{
    @Override
    public QuoteHolder jsonToQuoteHolder(JsonNode jsonResponse, QuoteCategory quoteCategory) {

        QuoteHolder quoteHolder = new QuoteHolder();
        if (quoteCategory.fieldName.equals("quote")){
            String author=jsonResponse.get("author").asText();
            quoteHolder.setQuote(jsonResponse.get(quoteCategory.fieldName).asText()+" "+ author);
        }
        else {
            quoteHolder.setQuote(jsonResponse.get(quoteCategory.fieldName).asText());
        }
        quoteHolder.setCategory(quoteCategory.fieldName);
        return quoteHolder;
    }
}
