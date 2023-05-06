package ch.uzh.ifi.hase.soprafs23.constant.ExtractApiCallData;

import ch.uzh.ifi.hase.soprafs23.entity.quote.FactHolder;
import com.fasterxml.jackson.databind.JsonNode;

public interface FactJsonExtractor {
    FactHolder jsonToFactHolder(JsonNode jsonResponse);
}
