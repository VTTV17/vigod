package pages.dashboard.promotion.discount.product_discount_code;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductDiscountCodeElement {
    WebDriver driver;

    public ProductDiscountCodeElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "input#name")
    WebElement CAMPAIGN_NAME;

    @FindBy(css = "div.col-xl-12 > div[class = ' '] > label:nth-child(1)")
    WebElement APPLY_DISCOUNT_CODE_AS_REWARD_CHECKBOX;

    @FindBy(css = "[name = 'rewardsDescription'] .show-placeholder > div")
    WebElement DISCOUNT_DESCRIPTION;

    @FindBy(css = "[novalidate] span > span.gs-fake-link")
    WebElement GENERATE_CODE_BTN;

    @FindBy(css = "#couponCode")
    WebElement DISCOUNT_CODE;

    @FindBy(css = ".expired-date label:nth-child(1)")
    WebElement NO_EXPIRY_DATE_CHECKBOX;

    @FindBy(css = "#couponDateRange")
    WebElement ACTIVE_DATE;

    //0: today
    //1: next day ...
    @FindBy(css = "td.available")
    List<WebElement> AVAILABLE_DATE;

    @FindBy(css = ".applyBtn")
    WebElement APPLY_BTN;

    //0: Percentage
    //1: Fixed amount
    //2: Free shipping
    @FindBy(css = "input[name ='couponType']")
    List<WebElement> TYPE_OF_DISCOUNT_LABEL;

    //0: GHN
    //1: GHTK
    //2: Ahamove
    //3: Self-delivery
    @FindBy (css = "[name = 'couponType'] label.custom-check-box")
    List<WebElement> SHIPPING_METHOD;

    @FindBy(css = "fieldset[name ='couponType'] > div > div > div > div > input")
    WebElement DISCOUNT_VALUE;

    //0: Limit number of times this coupon can be used
    //1: Limit to one use per customer
    @FindBy(css = ".col-xl-8 >div[class = ' '] > label:nth-child(1)")
    List<WebElement> USAGE_LIMIT_CHECKBOX;

    @FindBy(css = "form[novalidate] > div > div > input")
    WebElement NUMBER_OF_USED_TIMES;

    //0: All customers
    //1: Specific segment
    @FindBy(css = "input[name ='conditionCustomerSegment']")
    List<WebElement> CUSTOMER_SEGMENT_LABEL;

    @FindBy(css = "fieldset[name ='conditionCustomerSegment'] .gs-fake-link")
    WebElement ADD_SEGMENT_BTN;

    @FindBy(css = "[class^='search'] > div > div > input")
    WebElement SEARCH_BOX;

    @FindBy(css = "span[class ^= 'check-all'] > label")
    WebElement SELECT_ALL;

    @FindBy(css = ".modal-body > .footer-btn > .gs-button__green")
    WebElement OK_BTN;

    //0: Entire order
    //1: Specific product collections
    //2: Specific products
    @FindBy(css = "fieldset[name ='conditionAppliesTo'] label")
    List<WebElement> APPLIES_TO_LABEL;

    @FindBy(css = "fieldset[name ='conditionAppliesTo'] .gs-fake-link")
    WebElement ADD_COLLECTION_OR_PRODUCT_BTN;

    //0: None
    //1: Minimum purchase amount (Only satisfied products)
    //2: Minimum quantity of satisfied products
    @FindBy(css = "fieldset[name = 'conditionMinReq'] label")
    List<WebElement> MINIMUM_REQUIREMENTS;

    @FindBy(css = "fieldset[name = 'conditionMinReq'] > div > div > div > div > input")
    WebElement MINIMUM_PURCHASE_AMOUNT_OR_QUANTITY;

    //0: All branches
    //1: Specific branch
    @FindBy(css = "input[name ='conditionAppliesToBranch']")
    List<WebElement> APPLICABLE_BRANCH_LABEL;

    @FindBy(css = "fieldset[name ='conditionAppliesToBranch'] .gs-fake-link")
    WebElement SELECT_BRANCH_BTN;

    //0: Web
    //1: App
    //2: In-store
    @FindBy(css = "fieldset[name = 'conditionPlatform'] label")
    List<WebElement> PLATFORM;

    @FindBy(css = ".gs-button__green")
    WebElement SAVE_BTN;

    @FindBy(css = ".gs-page-title")
    WebElement PAGE_TITLE;

}
