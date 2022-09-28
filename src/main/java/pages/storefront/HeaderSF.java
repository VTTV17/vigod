package pages.storefront;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static java.lang.Thread.sleep;

public class HeaderSF {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    public static int countFail = 0;
    final static Logger logger = LogManager.getLogger(HeaderSF.class);

    public HeaderSF(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }

    @FindBy(css = ".navbar-brand.nav-link")
    WebElement USER_INFO_ICON;
    
    @FindBy(css = "#dropdown-menu-profile > :nth-child(1)")
    WebElement USER_PROFILE;

    @FindBy(id = "btn-login")
    WebElement LOGIN_ICON;    

    @FindBy(id = "btn-signup")
    WebElement SIGNUP_ICON;    
    
    @FindBy(xpath = "//div[@id='custom-search-input']")
    WebElement SEARCH_FIELD_TO_CLICK;

    @FindBy(xpath = "//input[@type='search']")
    WebElement SEARCH_FIELD_TO_INPUT;

    @FindBy (css = ".lds-ellipsis")
    WebElement SEARCH_LOADING;
    @FindBy(xpath = "//h3[contains(@class,'search-result-item-title')]")
    List<WebElement> SEARCH_SUGGESTION_RESULT_TITLE;

    @FindBy(xpath = "//strong[contains(@class,'search-result-item-price')]")
    List<WebElement> SEARCH_SUGGESTION_RESULT_PRICE;

    @FindBy (id = "btn-logout")
    WebElement LOGOUT_BTN;

    @FindBy(css = "#header-search-web-component")
    WebElement SEARCH_ICON;

    @FindBy(css = "input[name='q']")
    WebElement SEARCH_BOX;

    @FindBy(css = "div[class *= 'search-result-container'] > a")
    List<WebElement> LIST_SEARCH_RESULT;

    public HeaderSF clickUserInfoIcon() {
    	commons.clickElement(USER_INFO_ICON);
    	logger.info("Clicked on User Info icon.");
        return this;
    }
    
    public void clickUserProfile() {
    	commons.clickElement(USER_PROFILE);
    	logger.info("Clicked on User Profile linktext.");
    }

    public void clickLoginIcon() {
    	commons.clickElement(LOGIN_ICON);
    	logger.info("Clicked on Login icon.");
    }

    public void clickSignupIcon() {
    	commons.clickElement(SIGNUP_ICON);
    	logger.info("Clicked on Signup icon.");
    }
    
    public HeaderSF searchWithFullName(String fullName){
        commons.clickElement(SEARCH_FIELD_TO_CLICK);
        logger.info("Click on Search bar");
        commons.inputText(SEARCH_FIELD_TO_INPUT,fullName);
        logger.info("Input: %s into search field".formatted(fullName));
        return this;
    }
    public HeaderSF verifySearchSuggestion(String fullName, String price){
        commons.waitForElementVisible(SEARCH_LOADING);
        commons.waitForElementInvisible(SEARCH_LOADING);
        String searchSuggestionItem1_Title = commons.getText(SEARCH_SUGGESTION_RESULT_TITLE.get(0));
        Assert.assertEquals(searchSuggestionItem1_Title,fullName);
        logger.info("Verify name: %s display on search suggestion".formatted(fullName));
        String searchSuggestionItem1_Price = commons.getText(SEARCH_SUGGESTION_RESULT_PRICE.get(0));
        Assert.assertEquals(String.join("",searchSuggestionItem1_Price.split(",")), price);
        logger.info("Verify price: %s display on search suggestion".formatted(price));
        return this;
    }
    public void clickSearchResult (){
        commons.clickElement(SEARCH_SUGGESTION_RESULT_TITLE.get(0));
        logger.info("Click on the first suggestion to go to detail page");
    }

    public void clickLogout(){
        commons.clickElement(LOGOUT_BTN);
        logger.info("Clicked on Logout linktext");
    }

    /**
     * <p> If have product match condition, hasResult = false</p>
     * <p> Else hasResult = true</p>
     */
    public void checkProductIsDisplayOrHide(Boolean isDisplay, String productName) throws IOException, InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_ICON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(productName);

        // If have product match condition, actDisplay = false
        // Else actDisplay = true
        sleep(1000);
        boolean actDisplay = LIST_SEARCH_RESULT.size() > 0;
        countFail = new AssertCustomize(driver).assertTrue(countFail, actDisplay == isDisplay, "[Failed] Product display should be %s but it is %s".formatted(isDisplay, actDisplay));
    }

    /**
     * <p> countFail: The number of failure cases in this test</p>
     * <p> If countFail > 0, some cases have been failed</p>
     * <p> Reset countFail for the next test</p>
     */
    public void completeVerify() {
        if (countFail > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(countFail));
        }
        countFail = 0;
    }
}
