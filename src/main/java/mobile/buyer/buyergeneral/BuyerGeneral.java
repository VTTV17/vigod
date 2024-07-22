package mobile.buyer.buyergeneral;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class BuyerGeneral extends UICommonMobile {
    final static Logger logger = LogManager.getLogger(BuyerGeneral.class);

    WebDriver driver;
    WebDriverWait wait;
    public BuyerGeneral(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    By BACK_ICON = By.xpath("//android.widget.ImageView[contains(@resource-id,'action_bar_basic_img_back')]");
    By el_loading_icon = By.xpath("//*[contains(@resource-id,'fragment_loading_progress_bar')]");
    By HEADER_SEARCH_ICON = By.xpath("//*[ends-with(@resource-id,'fragment_select_region_btn_search')]");
    By HEADER_SEARCH_INPUT = By.xpath("//*[ends-with(@resource-id,'search_src_text')]");
    By SEARCH_RESULT_LIST = By.xpath("//*[ends-with(@resource-id,'item_list_region_name')]");
    By CHECKOUT_FOOTER_CONTINUE_BTN = By.xpath("//*[ends-with(@resource-id,'activity_shopping_cart_checkout_btn_next_or_confirm')]");
    By SEARCH_RESULT_CHECKED_ICON_1 = By.xpath("(//*[ends-with(@resource-id,'item_list_region_root')])[1]//*[ends-with(@resource-id,'item_list_region_img_checked')]");
    By HEADER_CLOSE_ICON = By.xpath("//*[ends-with(@resource-id,'fragment_select_region_btn_close')]");
    By HEADER_CANCEL_SEARCH = By.xpath("//*[ends-with(@resource-id,'search_module_btn_cancel')]");
    By TOASTMESSAGE = By.xpath("//*[ends-with(@class,'Toast')]");
    By DATE_PICKER_YEAR = By.xpath("//*[ends-with(@resource-id,'date_picker_year')]");
    By DATE_PICKER_DAY_FRAME = By.xpath("//*[ends-with(@resource-id,'animator')]");
    By YEAR_LIST = By.xpath("//*[ends-with(@resource-id,'month_text_view')]");

    public BuyerGeneral waitLoadingDisappear(){
        waitTillElementDisappear(getElement(el_loading_icon,30),30);
        logger.info("Wait loading...");
        return this;
    }
    public BuyerGeneral waitInMiliSecond(long milisecond){
        sleepInMiliSecond(milisecond);
        return this;
    }
    public BuyerGeneral clickOnBackIcon(){
        clickElement(BACK_ICON);
        logger.info("Click on back icon");
        return this;
    }
    public BuyerGeneral searchOnHeader(String keyword){
        clickElement(HEADER_SEARCH_ICON);
        inputText(HEADER_SEARCH_INPUT,keyword);
        sleepInMiliSecond(2000);
        if((getText(getElements(SEARCH_RESULT_LIST).get(0)).toLowerCase()).contains(keyword.toLowerCase())){
            if(isElementDisplay(SEARCH_RESULT_CHECKED_ICON_1)){
                tapCancelSearch();
                clickElement(HEADER_CLOSE_ICON);
            }else {
                clickElement(getElements(SEARCH_RESULT_LIST).get(0));
            }
            logger.info("Select: "+keyword);
        }else try {
            throw new Exception("The first result not match keywork");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    public void tapOnContinueBtn_Checkout(){
        clickElement(CHECKOUT_FOOTER_CONTINUE_BTN);
    }
    public BuyerGeneral tapCancelSearch(){
        clickElement(HEADER_CANCEL_SEARCH);
        logger.info("Tap on cancel search.");
        return this;
    }

    public String getToastMessage() {
    	String text = getText(TOASTMESSAGE);
    	logger.info("Retrieved toast message: " + text);
    	return text;
    }
    public BuyerGeneral verifyToastMessage(String expected){
        Assert.assertEquals(getToastMessage(),expected);
        logger.info("Verify toast message.");
        return this;
    }
}
