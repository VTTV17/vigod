package pages.dashboard.promotion.discount.product_wholesale_campaign;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductWholesaleCampaignElement {
    WebDriver driver;

    public ProductWholesaleCampaignElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "input#name")
    WebElement CAMPAIGN_NAME;

    @FindBy(css = "label.custom-check-box > div")
    WebElement NO_EXPIRY_DATE_CHECKBOX;

    @FindBy(css = "#couponDateRange")
    WebElement ACTIVE_DATE;

    // available day in current month
    @FindBy(css = "div:not(.right)> div > table > tbody> tr > .available:not(.prev):not(.off):not(.next)")
    List<WebElement> AVAILABLE_DATE;

    @FindBy (css = ".left  .month")
    WebElement CURRENT_MONTH;

    @FindBy (css = ".next")
    WebElement NEXT_BTN;

    @FindBy(css = ".applyBtn")
    WebElement APPLY_BTN;

    //0: Percentage
    //1: Fixed amount
    @FindBy(css = "input[name ='couponType']")
    List<WebElement> TYPE_OF_DISCOUNT_LABEL;

    @FindBy(css = "fieldset[name ='couponType'] > div > div > div > div > input")
    WebElement DISCOUNT_VALUE;

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

    @FindBy(css = "fieldset[name ='conditionAppliesTo'] label")
    List<WebElement> APPLIES_TO_LABEL;

    @FindBy(css = "fieldset[name ='conditionAppliesTo'] .gs-fake-link")
    WebElement ADD_COLLECTION_OR_PRODUCT_BTN;

    @FindBy(css = "div.col-xl-8 > div > div > input")
    WebElement MINIMUM_REQUIREMENTS;

    //0: All branches
    //1: Specific branch
    @FindBy(css = "input[name ='conditionAppliesToBranch']")
    List<WebElement> APPLICABLE_BRANCH_LABEL;

    @FindBy(css = "fieldset[name ='conditionAppliesToBranch'] .gs-fake-link")
    WebElement SELECT_BRANCH_BTN;

    @FindBy(css = ".gs-button__green")
    WebElement SAVE_BTN;

    @FindBy(css = ".gs-page-title")
    WebElement PAGE_TITLE;
}
