package pages.buyerapp.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;

public class BuyerSearchDetailPage extends UICommonAction{
    final static Logger logger = LogManager.getLogger(BuyerSearchDetailPage.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public BuyerSearchDetailPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
    By el_search_results = By.xpath("//android.widget.TextView[contains(@resource-id,'adapter_hot_deal_item_name')]");
    By el_search_input = By.xpath("//android.widget.AutoCompleteTextView[contains(@resource-id,'search_src_text')]");
    public BuyerSearchDetailPage inputKeywordToSearch(String keyword){
        inputText(el_search_input,keyword);
        logger.info("Input %s into search field".formatted(keyword));
        return this;
    }
}
