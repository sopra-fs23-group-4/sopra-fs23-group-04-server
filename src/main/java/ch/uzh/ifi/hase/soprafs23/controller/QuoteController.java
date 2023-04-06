package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.QuoteCategory;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteCategoriesGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        QuoteCategory quoteCategory = QuoteCategory.getQuoteByType(category);

        QuoteHolder quoteHolder = quoteService.generateQuote(quoteCategory);

        return DTOMapper.INSTANCE.convertEntityToQuoteGetDTO(quoteHolder);
    }

    @GetMapping(value="/quotes")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public QuoteCategoriesGetDTO getQuoteCategories(){
          QuoteCategoriesHolder quoteCategories = quoteService.getCategories();

          return DTOMapper.INSTANCE.convertEntityToQuoteCategoriesGetDTO(quoteCategories);
    }

}
