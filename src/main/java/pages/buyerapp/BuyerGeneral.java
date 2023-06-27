package pages.buyerapp;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonMobile;

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
    By el_loading = By.id("com.mediastep.shop0003:id/fragment_loading_progress_bar");
    By HEADER_SEARCH_ICON = By.xpath("//*[ends-with(@resource-id,'fragment_select_region_btn_search')]");
    By HEADER_SEARCH_INPUT = By.xpath("//*[ends-with(@resource-id,'search_src_text')]");
    By SEARCH_RESULT_LIST = By.xpath("//*[ends-with(@resource-id,'item_list_region_name')]");
    By CHECKOUT_FOOTER_CONTINUE_BTN = By.xpath("//*[ends-with(@resource-id,'activity_shopping_cart_checkout_btn_next_or_confirm')]");

    public BuyerGeneral waitLoadingDisappear(){
        waitTillElementDisappear(getElement(el_loading,30),30);
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
        sleepInMiliSecond(500);
        if((getText(getElements(SEARCH_RESULT_LIST).get(0))).contains(keyword)){
            clickElement(getElements(SEARCH_RESULT_LIST).get(0));
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
}
