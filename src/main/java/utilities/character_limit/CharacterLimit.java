package utilities.character_limit;

public class CharacterLimit {

    // product information
    public final static int MAX_PRODUCT_NAME = 100;
    //    final int MAX_PRODUCT_DESCRIPTION = 100000;
    public final static int MAX_PRODUCT_DESCRIPTION = 1000;
        public final static int MAX_STOCK_QUANTITY = 1000000;
    public final static int MAX_STOCK_QUANTITY_IMEI = 10;
    public static Long MAX_PRICE = 99999999999L;

    public final static int MAX_VARIATION_NAME = 14;
    public final static int MAX_VARIATION_VALUE = 20;
    public final static int MAX_VARIATION_QUANTITY = 2;
    //    public final static int MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION = 20;
    public final static int MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION = 5;
//    public final static int MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS = 50;

    public final static int MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS = 5;
    public final static int MAX_WEIGHT = 1000000;
    public final static int MAX_LENGTH = 100;
    public final static int MAX_WIDTH = 100;
    public final static int MAX_HEIGHT = 100;

    // product conversion unit
    public final static int MAX_CONVERSION_UNIT_NAME = 30;

    public final static int MAX_CONVERSION_UNIT_QUANTITY = 1000;
    public final static int MAX_CONVERSION_NAME = 30;

    public final static int MAX_DEPOSIT_QUANTITY = 20;
    // set for test
    public final static int MAX_DEPOSIT_NAME = 20;

    public final static int MAX_WHOLESALE_PRICE_TITLE = 30;

    public final static int MAX_WHOLESALE_NUM_PER_VAR = 20;
//    public final static int MAX_WHOLESALE_NUM_PER_VAR = 2000;


    // product collection:
    public final static int MAX_PRODUCT_COLLECTION_NAME_LENGTH = 50;
    public final static int MIN_PRODUCT_COLLECTION_NAME_LENGTH = 3;


    // promotion
    // flash sale
    public final static int MAX_FLASH_SALE_CAMPAIGN_NAME = 50;
    public final static int MIN_FLASH_SALE_CAMPAIGN_NAME = 3;

    // discount campaign
    public final static int MAX_PRODUCT_WHOLESALE_CAMPAIGN_NAME = 255;
    public final static int MAX_PROMOTION_DATE = 365;

    public final static int MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE = 2;
    public final static int MAX_PERCENT_DISCOUNT = 100;
    //    public final static int MAX_FIXED_AMOUNT = 1000000000;
    public final static int MAX_FIXED_AMOUNT = 10000000;
    public final static int MAX_PRODUCT_WHOLESALE_CAMPAIGN_SEGMENT_TYPE = 2;
    public final static int MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLIES_TO_TYPE = 3;
    public final static int MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE = 2;

    // product discount code
    public final static int MAX_PRODUCT_CODE_DISCOUNT_TYPE = 3;
    public final static int MAX_PRODUCT_DISCOUNT_CODE_NAME_LENGTH = 255;
    public final static int MAX_PRODUCT_DISCOUNT_CODE_LENGTH = 20;
    public final static int MIN_PRODUCT_DISCOUNT_CODE_LENGTH = 3;
    public final static int MAX_FREE_SHIPPING = 1000000000;
    public final static int MAX_REWARD_DESCRIPTION_LENGTH = 100000;
    public final static int MAX_COUPON_USED_NUM = 1000000000;
    public final static int MAX_PRODUCT_DISCOUNT_CODE_SEGMENT_TYPE = 2;
    public final static int MAX_PRODUCT_DISCOUNT_CODE_APPLIES_TO_TYPE = 3;
    public final static int MAX_PRODUCT_DISCOUNT_CODE_MINIMUM_REQUIREMENT_TYPE = 3;
    public final static int MAX_PRODUCT_DISCOUNT_CODE_APPLICABLE_BRANCH_TYPE = 2;


    // Customers >> All customers
    public final static int MAX_CUSTOMER_NAME = 100;
    public final static int MAX_PHONE_NUMBER = 15;
    public final static int MIN_PHONE_NUMBER = 8;
    public final static int MAX_CUSTOMER_TAG_NUM = 20;
    public final static int MAX_CUSTOMER_TAG_LENGTH = 20;


    // Segment
    public final static int MAX_SEGMENT_NAME_LENGTH = 100;
    public final static int MIN_SEGMENT_NAME_LENGTH = 1;


    // Marketing
    // Loyalty Program
    public final static int MAX_MEMBERSHIP_NAME = 150;
    public final static int MAX_MEMBERSHIP_DESCRIPTION_LENGTH = 100000;

    // SF
    // Sign up
//    public final static int MAX_BUYER_NAME_LENGTH = 128;
    public final static int MAX_BUYER_NAME_LENGTH = 100;
    public final static int MAX_BUYER_PASSWORD_LENGTH = 16;
    public final static int MIN_BUYER_PASSWORD_LENGTH = 8;

    public static final int MAX_CHAR_SERVICE_NAME = 100;
    public static final int MAX_CHAR_SEO_TITLE = 200;
    public static final int MAX_CHAR_SEO_DESCRIPTION = 325;
    public static final int MAX_CHAR_ADDRESS = 255;
}
