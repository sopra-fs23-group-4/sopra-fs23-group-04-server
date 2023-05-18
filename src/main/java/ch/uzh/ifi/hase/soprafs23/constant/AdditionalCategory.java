package ch.uzh.ifi.hase.soprafs23.constant;

import java.util.Random;

public enum AdditionalCategory {
    ALCOHOLIC_DRINKS("Alcoholic Drink"),
    APP("App"),
    ATHLETE("Athlete (General)"),
    AUTHOR("Author"),
    BANDS("Band"),
    BEER_BRANDS("Beer Brand"),
    BODY_PART("Body Part"),
    BOOK("Book"),
    BARS("Bar"),
    CANDIES("Candy"),
    CAR_BRAND("Car Brand"),
    CAKE("Cake"),
    CAT_BREED("Cat Breed"),
    CELEBRITIES("Celebrity"),
    CLOTHING("Clothing"),
    COLOR("Color"),
    COMIC_CHARACTER("Comic Character"),
    COSMETIC_PRODUCT("Cosmetic Product"),
    CURSE_WORD("Curse Word"),
    DISEASE("Disease"),
    DOG_BREED("Dog Breed"),
    EMOTION("Emotion"),
    FAST_FOOD("Fast Food"),
    FASHION_BRAND("Fashion Brand"),
    FIRST_NAME("First Name"),
    FISH_SPECIES("Fish Species"),
    FLOWERS("Flower"),
    FOOTBALL_CLUB("Football Club"),
    FRUIT("Fruit"),
    FUNGI("Fungus"),
    GEMSTONE("Gemstone"),
    HERBS("Herb"),
    HOBBY("Hobby"),
    INSECT("Insect"),
    ISLAND("Island"),
    ITEM_ON_DESK("Item on the Desk"),
    LANGUAGE("Language"),
    LAKE("Lake"),
    MAIN_COURSE("Main Course"),
    MARINE_ANIMAL("Marine Animal"),
    MOUNTAIN("Mountain"),
    MOVIES("Movie"),
    MUSHROOM_TYPE("Mushroom Type"),
    NATURAL_DISASTERS("Natural Disaster"),
    NEWSPAPER_MAG("Newspaper/Magazine"),
    NICKNAME("Nickname"),
    PET_NAMES("Pet Name"),
    PIZZA_TOPPING("Pizza Topping"),
    PLANT("Plant"),
    POLITICIANS("Politician"),
    RESTAURANTS("Restaurant"),
    SCHOOL_SUBJECT("School Subject"),
    SEA("Sea"),
    SINGER("Singer"),
    SINGERS("Singer"),
    SOFT_DRINKS("Soft Drink"),
    SONG("Song"),
    SPICES("Spice"),
    SPORT("Sport"),
    SUPERHEROES("Superhero"),
    SUPERMARKETS("Supermarket"),
    SWEET("Sweet"),
    TOOL("Tool"),
    TOY("Toy"),
    TRASH_TV("Trash TV"),
    TREE("Tree"),
    TV_CHANNEL("TV Channel"),
    TV_SHOW("TV Show"),
    VEGETABLE("Vegetable");

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
