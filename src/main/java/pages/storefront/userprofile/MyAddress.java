package pages.storefront.userprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

import java.time.Duration;

public class MyAddress {
	
	final static Logger logger = LogManager.getLogger(MyAddress.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public MyAddress (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "countryCode")
    WebElement COUNTRY;
    
    public String getCountry() {
		String js = "var e=arguments[0], i=e.selectedIndex; return i < 0 ? null : e.options[i];";
		WebElement selectedOption = (WebElement) ((JavascriptExecutor) driver).executeScript(js, COUNTRY);
		if (selectedOption == null) throw new NoSuchElementException("No options are selected");
		String value = selectedOption.getText();
    	logger.info("Retrieved Country: " + value);
        return value;
    }
    
}
