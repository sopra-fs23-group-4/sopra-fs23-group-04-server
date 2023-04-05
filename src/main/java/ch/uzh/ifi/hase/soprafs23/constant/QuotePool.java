package ch.uzh.ifi.hase.soprafs23.constant;

public enum QuotePool {

    DADJOKE("https://api.api-ninjas.com/v1/dadjokes?limit=1");

    public final String url;

    QuotePool(String url) {
        this.url = url;
    }
}
