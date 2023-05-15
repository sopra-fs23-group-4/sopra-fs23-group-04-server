package ch.uzh.ifi.hase.soprafs23.websocketDto;

public class FactDTO {
    public static final String TYPE = "fact";
    public String getType(){
        return TYPE;
    }
    public String fact;

    public void setFact(String fact) {
        this.fact = fact;
    }

    public String getFact() {
        return fact;
    }
}
