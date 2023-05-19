package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.quote.FactHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteCategoriesHolder;
import ch.uzh.ifi.hase.soprafs23.entity.quote.QuoteHolder;
import ch.uzh.ifi.hase.soprafs23.rest.dto.quote.QuoteGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.QuoteService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import jdk.jshell.Snippet;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(QuoteController.class)
class QuoteControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private QuoteService quoteService;

    @Test
    void givenCategory_whenGetQuote_thenReturnJson() throws Exception {

        // Given
        String category = "inspirational";
        QuoteHolder quoteHolder = new QuoteHolder();
        quoteHolder.setQuote("this is a quote");
        quoteHolder.setCategory(category);
        QuoteGetDTO quoteGetDTO = new QuoteGetDTO();
        quoteGetDTO.setQuote("this is a quote");


        // Mock the QuoteService to return the test data
        given(quoteService.generateQuote(category)).willReturn(quoteHolder);

        // When
        MockHttpServletRequestBuilder getRequest = get("/quotes/{category}", category)
                .contentType(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.quote", Matchers.is(quoteHolder.getQuote())));

    }
    @Test
    void invalidCategory() throws Exception {
        String category = "invalid";



        // Mock the QuoteService to return the test data
        given(quoteService.generateQuote(category)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // When
        MockHttpServletRequestBuilder getRequest = get("/quotes/{category}", category)
                .contentType(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());

    }
    @Test
    void testGetFact() throws Exception {
        // Mock the behavior of the quoteService.generateFact() method
        FactHolder mockFactHolder = new FactHolder();
        mockFactHolder.setFact("This is a fact");
        // Set up your mockFactHolder object as per your requirements

        given(quoteService.generateFact()).willReturn(mockFactHolder);


        MockHttpServletRequestBuilder getRequest = get("/facts")
                .contentType(MediaType.APPLICATION_JSON);
        // Perform the GET request
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.fact", Matchers.is(mockFactHolder.getFact())));
        // Update the jsonPath assertion with the appropriate property of FactHolder class
    }

    @Test
    void testGetQuoteCategories() throws Exception {
        // Mock the behavior of the QuoteService.getCategories() method
        QuoteCategoriesHolder quoteCategoriesHolder = new QuoteCategoriesHolder();

        List<String> mockCategories = Arrays.asList("Category1", "Category2", "Category3");
        quoteCategoriesHolder.setCategories(mockCategories);
        // Set up your mockCategories list as per your requirements

        given(quoteService.getCategories()).willReturn(quoteCategoriesHolder);

        // Perform the GET request

        MockHttpServletRequestBuilder getRequest = get("/quotes")
                .contentType(MediaType.APPLICATION_JSON);
        // Perform the GET request
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.categories").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories.length()").value(mockCategories.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories[0]").value(mockCategories.get(0)));
        // Update the jsonPath assertions with the appropriate properties of QuoteCategoriesGetDTO class
    }






}
