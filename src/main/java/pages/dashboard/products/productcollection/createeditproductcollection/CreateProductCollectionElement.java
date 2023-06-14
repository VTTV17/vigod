package pages.dashboard.products.productcollection.createeditproductcollection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CreateProductCollectionElement {
    WebDriver driver;
    public CreateProductCollectionElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(id = "collectionName")
    WebElement COLLECTION_NAME_INPUT;
    @FindBy(css = ".image-drop-zone input")
    WebElement IMAGE_INPUT;
    @FindBy(id = "radio-collectionType-MANUAL")
    WebElement MANUAL_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-collectionType-MANUAL']/following-sibling::label")
    WebElement MANUAL_RADIO_ACTION;
    @FindBy(id = "radio-collectionType-AUTOMATED")
    WebElement AUTOMATED_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-collectionType-AUTOMATED']/following-sibling::label")
    WebElement AUTOMATED_RADIO_ACTION;
    @FindBy(id = "radio-conditionType-ALL")
    WebElement ALL_CONDITION_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-conditionType-ALL']/parent::div")
    WebElement ALL_CONDITION_RADIO_ACTION;
    @FindBy(id = "radio-conditionType-ANY")
    WebElement ANY_CONDITION_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-conditionType-ANY']/parent::div")
    WebElement ANY_CONDITION_RADIO_ACTION;
    @FindBy(id = "conditionField")
    List<WebElement> CONDITION_DROPDOWN;
    @FindBy(id = "conditionOperand")
    List<WebElement> OPERATOR_DROPDOWN;
    @FindBy(css = ".uik-input__input")
    List<WebElement> CONDITION_VALUE_INPUT;
    @FindBy(css = ".btn-addproduct")
    WebElement SELECT_PRODUCT_BTN;
    @FindBy(xpath = "//input[@name='check_all']")
    WebElement SELECT_ALL_CBX_VALUE;
    @FindBy(xpath = "//input[@name='check_all']//following-sibling::div")
    WebElement SELECT_ALL_CBX_ACTION;
    @FindBy(css = ".search-input")
    WebElement SEARCH_FOR_PRODUCT_INPUT;
    @FindBy(css = ".footer-btn .gs-button__green")
    WebElement OK_BTN;
    @FindBy(css = ".automated-config__header button > div")
    WebElement ADD_MORE_CONDITION_BTN;
    @FindBy(id = "seoTitle")
    WebElement SEO_TITLE;
    @FindBy(id = "seoDescription")
    WebElement SEO_DESCRIPTION;
    @FindBy(id = "seoKeywords")
    WebElement SEO_KEYWORD;
    @FindBy(id = "seoUrl")
    WebElement SEO_URL;
    @FindBy(css = ".modal-footer button")
    WebElement CLOSE_BTN;
    @FindBy(css = ".header-wrapper .gs-button__green")
    WebElement SAVE_BTN;
    @FindBy(xpath = "//td[contains(@class,'gs-table-body-item')]//input[@precision='0']")
    List<WebElement> PRIORITIES_INPUT;
    @FindBy(xpath = "//td[contains(@class,gs-table-body-item)][2]//b")
    List<WebElement> PRODUCT_NAME_LIST;
    @FindBy(css = ".btn-remove__row")
    List<WebElement> DELETE_BTN_LIST;
    @FindBy(css = ".btn-remove__row")
    WebElement DELETE_BTN_1;
    @FindBy(css = ".gs-page-title")
    WebElement CREATE_PRODUCT_COLLECTION_TITLE;
    @FindBy(xpath = "//div[contains(@class,'action-btn--group')]//button[2]")
    WebElement CANCEL_BTN;
    @FindBy(xpath = "//button[@type='submit']//following-sibling::div[1]//h3")
    WebElement GENERAL_INFOMATION_TITLE;
    @FindBy(xpath = "//label[@for='collectionName']")
    WebElement COLLECTION_NAME_LBL;
    @FindBy(xpath = "//label[@for='collectionName']//following-sibling::label")
    WebElement IMAGES_LBL;
    @FindBy(xpath = "//input[@type='file']/following-sibling::div[2]")
    WebElement DRAG_DROP_PHOTO_TXT;
    @FindBy(xpath = "(//fieldset[@name='collectionType']/preceding::div//h3)[last()]")
    WebElement COLLECTION_TYPE_TITLE;
    @FindBy(xpath = "//div[@class='collection-description'][1]")
    WebElement MANUAL_TYPE_DESCRIPTION;
    @FindBy(xpath = "//div[@class='collection-description'][2]")
    WebElement AUTOMATED_TYPE_DESCRIPTION;
    @FindBy(xpath = "//div[contains(@class,'collection-filter-and-sort')]//h3")
    WebElement FILTER_SORT_OPTION_TITLE;
    @FindBy(xpath = "//div[@class='filter-and-sort-text'][1]")
    WebElement FILTER_OPTION_LBL;
    @FindBy(xpath = "//div[@class='filter-and-sort-text'][2]")
    WebElement SORT_OPTION_LBL;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][1]//span)[1]")
    WebElement FILTER_OPTION_PRICE_RANGE_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][1]//span)[2]")
    WebElement FILTER_OPTION_VARIATION_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][1]//span)[3]")
    WebElement FILTER_OPTION_PROMOTION_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][1]//span)[4]")
    WebElement FILTER_OPTION_UNIT_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][1]//span)[5]")
    WebElement FILTER_OPTION_BRANCH_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[1]")
    WebElement SORT_OPTION_PRICE_ASC_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[2]")
    WebElement SORT_OPTION_PRICE_DESC_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[3]")
    WebElement SORT_OPTION_NAME_A_Z_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[4]")
    WebElement SORT_OPTION_NAME_Z_A_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[5]")
    WebElement SORT_OPTION_OLDEST_TO_NEWEST_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[6]")
    WebElement SORT_OPTION_NEWEST_TO_OLDEST_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[7]")
    WebElement SORT_OPTION_BEST_SELLING_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[8]")
    WebElement SORT_OPTION_RATING_ASC_TXT;
    @FindBy(xpath = "(//div[@class='filter-and-sort-checkbox-group'][2]//span)[9]")
    WebElement SORT_OPTION_RATING_DESC_TXT;
    @FindBy(xpath = "//div[@class='bnt-group_add_product']//preceding-sibling::h3")
    WebElement PRODUCT_LIST_TITLE;
    @FindBy(xpath = "//div[@class='product-list__group']//span")
    WebElement NO_PRODUCT_TXT;
    @FindBy(xpath = "//button[@type='submit']//following-sibling::div[5]/child::div/h3")
    WebElement SEO_SETTINGS_TITLE;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span")
    WebElement LIVE_PREVIEW_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span/following-sibling::div//div")
    WebElement LIVE_PREVIEW_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]/span")
    WebElement SEO_TITLE_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]//div/div")
    WebElement SEO_TITLE_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]/span")
    WebElement SEO_DESCRIPTION_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]//div/div")
    WebElement SEO_DESCRIPTION_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]/span")
    WebElement SEO_KEYWORDS_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]//div/div")
    WebElement SEO_KEYWORD_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][4]/span")
    WebElement URL_LINK_LBL;
    @FindBy(xpath = "//h5[@class='modal-title']")
    WebElement MODAL_SELECT_PRODUCT_TITLE;
    @FindBy(xpath = "//div[@class='product-list-wrapper']//th[1]//div")
    WebElement MODAL_PRODUCT_NAME_COLUMN_TXT;
    @FindBy(xpath = "//div[@class='product-list-wrapper']//th[2]")
    WebElement MODAL_UNIT_COLUMN_TXT;
    @FindBy(xpath = "//div[@class='product-list-wrapper']//th[3]")
    WebElement MODAL_COST_PRICE_COLUMN_TXT;
    @FindBy(xpath = "//div[@class='product-list-wrapper']//th[4]")
    WebElement MODAL_LISTING_PRICE_COLUMN_TXT;
    @FindBy(xpath = "//div[@class='product-list-wrapper']//th[5]")
    WebElement MODAL_SELLING_PRICE_COLUMN_TXT;
    @FindBy(css = ".footer-btn .gs-button__gray--outline")
    WebElement MODAL_CANCEL_BTN;
    @FindBy(xpath = "//div[@class='automated-config__header']//h3")
    WebElement CONDITIONS_TILE;
    @FindBy(xpath = "//div[@class='automated-config__condition-type']/span[1]")
    WebElement PRODUCT_MUST_MATCH_TXT;


}
