package pages.buyerapp.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonMobile;

import java.time.Duration;

public class BuyerSearchPage extends UICommonMobile{
    final static Logger logger = LogManager.getLogger(BuyerSearchPage.class);

    WebDriver driver;
    WebDriverWait wait;
    BuyerSearchElement searchPage;


    public BuyerSearchPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        searchPage = new BuyerSearchElement(driver);
    }
    public BuyerSearchDetailPage tapOnSearchBar(){
        clickElement(searchPage.SEARCH_BAR);
        return new BuyerSearchDetailPage(driver);
    }
}
