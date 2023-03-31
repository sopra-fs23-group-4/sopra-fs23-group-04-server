package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuoteController {
  private final QuoteService quoteService;
  QuoteController(QuoteService quoteService){
    this.quoteService= quoteService;
  }

  @GetMapping(value = "/quotes/{category}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public QuoteGetDTO getQuote(@PathVariable("category") String category) throws Exception {
    QuoteCategory quoteCategory=QuoteCategory.getQuoteByType(category);

    QuoteHolder quoteHolder= quoteService.generateQuote(quoteCategory);

    return DTOMapper.INSTANCE.convertEntityToQuoteGetDTO(quoteHolder);


  }
}
