package pages.buyerapp.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.buyerapp.home.BuyerHomePage;
import utilities.UICommonAction;
import utilities.UICommonMobile;

import java.time.Duration;

public class BuyerSearchPage extends UICommonMobile{
    final static Logger logger = LogManager.getLogger(BuyerSearchPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commons;


    public BuyerSearchPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    By el_search_bar = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'btn_action_bar_search')]");
    public BuyerSearchDetailPage tapOnSearchBar(){
        clickElement(el_search_bar);
        return new BuyerSearchDetailPage(driver);
    }
}
