package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;

public interface QuoteApiCaller {
    String callApi(QuoteCategory quoteCategory, String apiKey);
}
