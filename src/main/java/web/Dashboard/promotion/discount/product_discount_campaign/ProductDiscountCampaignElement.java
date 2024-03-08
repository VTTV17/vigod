package web.Dashboard.promotion.discount.product_discount_campaign;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductDiscountCampaignElement {
    WebDriver driver;

    public ProductDiscountCampaignElement(WebDriver driver) {
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

    @FindBy (css = ".segment-name > span")
    List<WebElement> LIST_SEGMENT_NAME;

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
    public By loc_detail_lblDiscountCampaignName = By.xpath("(//div[@class = 'row'])[3]/div[2]");
    public By loc_txtCampaignName = By.cssSelector("input#name");
    public By loc_btnAddSegment = By.cssSelector("fieldset[name ='conditionCustomerSegment'] .gs-fake-link");
    public By loc_lst_lblSegmentName = By.cssSelector(".segment-name > span");
    public By loc_lst_chkCustomerSegment = By.cssSelector("input[name ='conditionCustomerSegment']");
    By loc_btnAddCollection = By.cssSelector("fieldset[name ='conditionAppliesTo'] .gs-fake-link");
    By loc_btnAddProduct = By.cssSelector("fieldset[name ='conditionAppliesTo'] .gs-fake-link");
    public By loc_lst_lblCollectionName = By.cssSelector(".product-name");
    public By loc_lst_lblProductName = By.cssSelector(".product-name");
    public By loc_txtSearch = By.cssSelector(".search-input");
    public By loc_cbxApplicableBranch = By.cssSelector("fieldset[name ='conditionAppliesToBranch'] label");
    By loc_btnSelectBranch = By.cssSelector("fieldset[name ='conditionAppliesToBranch'] .gs-fake-link");
    By loc_lst_lblBranchName = By.cssSelector(".branch-name");
    public By loc_btnSave  = By.cssSelector(".gs-button__green");
    public By loc_btnEndEarly = By.cssSelector(".discount-campaign__detail .btn-save div");
    By loc_dlgSelectSegment = By.cssSelector(".select-segment-modal");
    By loc_dlgSelectCollection = By.cssSelector(".select-collection-modal");

    By loc_dlgSelectProduct = By.cssSelector(".product-no-variation-modal");
}
