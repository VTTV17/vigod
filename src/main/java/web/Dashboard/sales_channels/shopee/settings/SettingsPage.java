package web.Dashboard.sales_channels.shopee.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAction;

import java.time.Duration;

public class SettingsPage extends SettingElements {

    final static Logger logger = LogManager.getLogger(SettingsPage.class);


    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public SettingsPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }


    public SettingsPage clickSave() {
        commonAction.click(loc_btnSave);
        logger.info("Clicked on 'Save' button.");
        return this;
    }
}
