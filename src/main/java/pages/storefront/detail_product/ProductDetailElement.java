package pages.storefront.detail_product;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductDetailElement {
    WebDriver driver;
    public ProductDetailElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#item-detail .title")
    WebElement PRODUCT_NAME;

    @FindBy (css = ".price-disc")
    WebElement SELLING_PRICE;

    @FindBy (css = ".price-org")
    WebElement LISTING_PRICE;

    @FindBy (css = "span[rv-text='variation.label']")
    List<WebElement> LIST_VARIATION_NAME;

    @FindBy (css = ".type-of-item span")
    List<WebElement> LIST_VARIATION_VALUE;

    @FindBy (css = "#branch-list .stock")
    List<WebElement> STOCK_QUANTITY_IN_BRANCH;

    @FindBy (css = "#product-description")
    WebElement PRODUCT_DESCRIPTION;

    @FindBy (css = ".sold-out")
    WebElement SOLD_OUT_MARK;

    @FindBy (css = ".info > div:nth-child(1)")
    List<WebElement> BRANCH_NAME_LIST;

    @FindBy (css = "[name = 'quantity']")
    WebElement QUANTITY;

    @FindBy (css = ".flash-sale")
    WebElement FLASH_SALE_BADGE;

    @FindBy (css = ".buy-in-bulk__checkbox")
    WebElement DISCOUNT_CAMPAIGN_CHECKBOX;

    @FindBy (css = ".product-wholesale-pricing")
    WebElement WHOLESALE_PRODUCT_INFO;

    @FindBy(css = "#button-buy-now")
    WebElement BUY_NOW_BTN;

    @FindBy (css = "#button-add-to-cart")
    WebElement ADD_TO_CART_BTN;
    By SPINNER = By.cssSelector(".loader");

    @FindBy (css = "#locationCode")
    WebElement FILTER_BRANCH;

    @FindBy (css = ".input-search-branch")
    WebElement SEARCH_BRANCH;


    // UI check
    @FindBy (css = ".tm-header-default-menu-layout img.gs-shop-logo")
    WebElement HEADER_SHOP_LOGO;

    @FindBy (css = ".navbar-desktop .nav-link")
    List<WebElement> HEADER_MENU ;

    @FindBy (css = ".bi-search")
    WebElement HEADER_SEARCH_ICON;

    @FindBy (css = ".shoppping-cart-number")
    WebElement HEADER_NUMBER_PRODUCT_IN_CART;

    @FindBy (css = ".bi-cart2")
    WebElement HEADER_CART_ICON;

    @FindBy (css = ".bi-person-circle")
    WebElement HEADER_PROFILE_ICON;

    @FindBy (css = ".breadcrumbs a")
    List<WebElement> BREAD_CRUMBS;

    @FindBy(css = ".quantity-box > div:nth-child(1)")
    WebElement QUANTITY_TITLE;

    @FindBy(css = ".branch-list > .title")
    WebElement AVAILABLE_BRANCH;

    @FindBy(css = "#locationCode > option:nth-child(1)")
    WebElement FILTER_BRANCH_BY_LOCATION;

    @FindBy(css = ".input-search-branch > input")
    WebElement SEARCH_BRANCH_BY_ADDRESS;

    @FindBy(css = ".payment .text")
    WebElement PAYMENT;

    @FindBy(css = "#nav-description-tab")
    WebElement DESCRIPTION_TAB;
    
    final String reviewTabLocator = "#nav-review-tab";
    @FindBy(css = reviewTabLocator)
    WebElement REVIEW_TAB;
    
    @FindBy(css = ".review-row")
    List<WebElement> REVIEWS;

    @FindBy(css = ".review-form .icon-star-solid")
    List<WebElement> RATING_STARS;
    
    @FindBy(css = ".review-form #title")
    WebElement REVIEW_TITLE;
    
    @FindBy(css = ".review-form #description")
    WebElement REVIEW_DESCRIPTION;
    
    @FindBy(css = ".review-form .btn-submit")
    WebElement SUBMIT_REVIEW_BTN;
    
    @FindBy(css = "#similar-product .title-product-description")
    WebElement SIMILAR_PRODUCT;

    @FindBy(css = "img.gs-shop-logo:not(.my-avatar)")
    WebElement FOOTER_SHOP_LOGO;

    @FindBy(css = ".col-sm-3 > .title")
    WebElement FOOTER_COMPANY;

    @FindBy(xpath = "//div[@class = 'icon-wrapper']/parent::div/div[contains(@class, 'title')]")
    WebElement FOOTER_FOLLOW_US;

    @FindBy(css = ".col-12.text-center > span")
    WebElement FOOTER_COPYRIGHT;

    @FindBy(css = "meta[name='title']")
    WebElement META_TITLE;

    @FindBy(css = "meta[name='description']")
    WebElement META_DESCRIPTION;

    @FindBy(css = "meta[name='keywords']")
    WebElement META_KEYWORD;

    @FindBy(css = "meta[name='og:url']")
    WebElement META_URL;
}
