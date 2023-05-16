package ch.uzh.ifi.hase.soprafs23.constant;

import java.util.Random;

public enum AdditionalCategory {
    ALCOHOLIC_DRINKS("Alcoholic Drinks"),
    APP("App"),
    ATHLETE("Athlete (General)"),
    AUTHOR("Author"),
    BANDS("Bands"),
    BEER_BRANDS("Beer Brands"),
    BODY_PART("Body Part"),
    BOOK("Book"),
    BREAKFAST("Breakfast"),
    BARS("Bars"),
    BIRD_SPECIES("Bird Species"),
    CANDIES("Candies"),
    CAR_BRAND("Car Brand"),
    CAKE("Cake"),
    CAUSE_OF_DEATH("Cause of Death"),
    CATEGORY_NAME("Category Name"),
    CAT_BREED("Cat Breed"),
    CELEBRITIES("Celebrities"),
    CLOTHING("Clothing"),
    COLOR("Color"),
    COMIC_CHARACTER("Comic Character"),
    COSMETIC_PRODUCT("Cosmetic Product"),
    CURSE_WORD("Curse Word"),
    DISNEY_CHARACTER("Disney Character"),
    DISEASE("Disease"),
    DOG_BREED("Dog Breed"),
    EMOTION("Emotion"),
    FAIRYTALE_CHARACTER("Fairytale Character"),
    FAST_FOOD("Fast Food"),
    FAMOUS_DECEASED("Famous Deceased"),
    FASHION_BRAND("Fashion Brand"),
    FIRST_NAME("First Name"),
    FISH_SPECIES("Fish Species"),
    FLOWERS("Flowers"),
    FOOTBALL_CLUB("Football Club"),
    FOOTBALL_PLAYER("Football Player"),
    FRUIT("Fruit"),
    FUNGI("Fungi"),
    GEMSTONE("Gemstone"),
    HERBS("Herbs"),
    HISTORICAL_PERSONALITY("Historical Personality"),
    HOBBY("Hobby"),
    ICE_CREAM_FLAVORS("Ice Cream Flavors"),
    INSECT("Insect"),
    ISLAND("Island"),
    ITEM_ON_DESK("Item on the Desk"),
    ITEM_SMALLER_OR_LARGER_THAN_XY("Item Smaller/Larger Than XY"),
    LANGUAGE("Language"),
    LAST_NAME("Last Name"),
    LAKE("Lake"),
    MAIN_COURSE("Main Course"),
    MARINE_ANIMAL("Marine Animal"),
    MOUNTAIN("Mountain"),
    MOVIES("Movies"),
    MUSHROOM_TYPE("Mushroom Type"),
    NATURAL_DISASTERS("Natural Disasters"),
    NEWSPAPER_MAGAZINE("Newspaper/Magazine"),
    NICKNAME("Nickname"),
    PET_NAMES("Pet Names"),
    PIZZA_TOPPING("Pizza Topping"),
    PLANT("Plant"),
    POLITICIANS("Politicians"),
    RESTAURANTS("Restaurants"),
    REASON_FOR_BEING_LATE("Reason for Being Late"),
    REASON_FOR_CELEBRATION("Reason for Celebration"),
    REASON_FOR_RESIGNATION("Reason for Resignation"),
    REASON_FOR_SEPARATION("Reason for Separation"),
    REASON_FOR_SKIPPING("Reason for Skipping"),
    SCHOOL_SUBJECT("School Subject/Study Field"),
    SEA("Sea"),
    SINGER("Singer"),
    SINGERS("Singers"),
    SOFT_DRINKS("Soft Drinks"),
    SONG("Song"),
    SPICES("Spices"),
    SPORT("Sport"),
    SUPERHEROES("Superheroes"),
    SUPERMARKETS("Supermarkets"),
    SWEET("Sweet"),
    TECHNICAL_DEVICE("Technical Device"),
    TOOL("Tool"),
    TOY("Toy"),
    TRASH_TV("Trash TV"),
    TREE("Tree"),
    TV_CHANNEL("TV Channel"),
    TV_SHOW("TV Show"),
    VEGETABLE("Vegetable"),
    WELL_KNOWN_COMPANY("Well-Known Company"),
    WORK_OF_ART("Work of Art");

    private final String value;
    private static final Random random = new Random();

    AdditionalCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getRandomCategoryName() {

        return AdditionalCategory.values()[random.nextInt(AdditionalCategory.values().length)].getValue();

    }

}
