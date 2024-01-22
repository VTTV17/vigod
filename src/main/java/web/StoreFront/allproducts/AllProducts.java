package web.StoreFront.allproducts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import web.GoMua.myprofile.MyProfileGoMua;
import utilities.commons.UICommonAction;

import java.time.Duration;

public class AllProducts {
    final static Logger logger = LogManager.getLogger(MyProfileGoMua.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction common;
    AllProductElement allProductUI;

    public AllProducts(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        allProductUI = new AllProductElement(driver);
        PageFactory.initElements(driver, this);
    }

}
