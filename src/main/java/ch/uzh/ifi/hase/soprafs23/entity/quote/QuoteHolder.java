package ch.uzh.ifi.hase.soprafs23.entity.quote;

public class QuoteHolder {
  public String getQuote() {
    return quote;
  }

  public void setQuote(String quote) {
    this.quote = quote;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  private String quote;
  private String type;
}