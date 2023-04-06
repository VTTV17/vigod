package pages.buyerapp.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class BuyerSearchElement {
    WebDriver driver;
    public BuyerSearchElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By SEARCH_BAR = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'btn_action_bar_search')]");

}
