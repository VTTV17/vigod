package web.Dashboard.onlineshop.themes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.staffPermission.AllPermissions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ThemeDetail {
    final static Logger logger = LogManager.getLogger(ThemeDetail.class);

    WebDriver driver;
    UICommonAction commonAction;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    public ThemeDetail(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    By loc_btnSave = By.cssSelector(".platform-action .gs-button__green");
    By loc_btnPushlish = By.cssSelector(".platform-action .gs-button__green--outline");
    By loc_txtThemeName = By.cssSelector("#customeName");
    By loc_icnSpinerComponent = By.cssSelector("#loading-iframe-content");
    public ThemeDetail clickOnSave(){
        commonAction.click(loc_btnSave);
        logger.info("Click on Save button");
        return this;
    }
    public ThemeDetail clickOnPublish(){
        commonAction.click(loc_btnPushlish);
        logger.info("Click on Publish button");
        return this;
    }
    public String inputThemeName(){
        String themeName = "Theme "+ new DataGenerator().randomNumberGeneratedFromEpochTime(5);
        commonAction.inputText(loc_txtThemeName,themeName);
        return themeName;
    }
    public void waitSpinerLoadOnComponenHidden(){
        WebDriverWait expliciWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        expliciWait.until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                String styleLoading = commonAction.getAttribute(loc_icnSpinerComponent,"style");
                if(styleLoading.contains("hidden")){
                    return true;
                }else {
                    return false;
                }
            }
        });
        logger.info("Wait component loaded.");
        new WebDriverWait(driver, Duration.ofSeconds(0));
    }
}
