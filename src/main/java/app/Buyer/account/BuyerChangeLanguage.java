package app.Buyer.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class BuyerChangeLanguage extends UICommonMobile {
    final static Logger logger = LogManager.getLogger(BuyerChangeLanguage.class);

    WebDriver driver;
    WebDriverWait wait;

    public BuyerChangeLanguage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    String LANGUAGE_BTN_DYNAMIC = "//android.widget.TextView[@text='%s']";
    By CHANGE_BTN = By.xpath("//android.widget.Button[contains(@resource-id,'button1')]");
    By SELECTED_LANGUAGE = By.xpath("//android.widget.ImageView[contains(@resource-id,'item_language_checkbox')]//preceding-sibling::android.widget.TextView");
    By HEADER_BACK_ICON = By.xpath("//*[ends-with(@resource-id,'fragment_general_more_settings_change_language_toolbar')]/android.widget.ImageButton");
    public void changeLanguage(String lang){
        String currentLanguage = getText(SELECTED_LANGUAGE);
        String selectLang = "";
        switch (lang){
            case "ENG"-> selectLang = "English";
            case "VIE" -> selectLang = "Vietnamese";
            default -> {
                try {
                    throw new Exception("Language not match!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(!selectLang.equalsIgnoreCase(currentLanguage)){
            sleepInMiliSecond(2000);
            String newXpach = LANGUAGE_BTN_DYNAMIC.formatted(selectLang);
            clickElement(getElementByXpath(newXpach));
            sleepInMiliSecond(200);
            clickElement(CHANGE_BTN);
            sleepInMiliSecond(2000);
        }else {
            clickElement(HEADER_BACK_ICON);
        }
    }
    public String getCurrentLanguage(){
        String currentLanguage = getText(SELECTED_LANGUAGE);
        switch (currentLanguage){
            case "English"-> {
                return "ENG";
            }
            case "Tiếng Việt" -> {
                return "VIE";
            }
            default -> {
                try {
                    throw new Exception("Language not match!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
