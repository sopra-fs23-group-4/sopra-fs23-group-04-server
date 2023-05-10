package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.quote.FactHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.FactGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteCategoriesGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuoteController {
    private final QuoteService quoteService;
    QuoteController(QuoteService quoteService){
    this.quoteService = quoteService;
    }

    @GetMapping(value = "/quotes/{category}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public QuoteGetDTO getQuote(@PathVariable("category") String category) throws Exception {

        QuoteHolder quoteHolder = quoteService.generateQuote(category);

        return UserDTOMapper.INSTANCE.convertEntityToQuoteGetDTO(quoteHolder);
    }

    @GetMapping(value="/quotes")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public QuoteCategoriesGetDTO getQuoteCategories(){

          QuoteCategoriesHolder quoteCategories = quoteService.getCategories();

          return UserDTOMapper.INSTANCE.convertEntityToQuoteCategoriesGetDTO(quoteCategories);
    }
    @GetMapping(value = "/facts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FactGetDTO getFact(){

        FactHolder factHolder= quoteService.generateFact();

        return UserDTOMapper.INSTANCE.convertEntityToFactGetDTO(factHolder);
    }


}
