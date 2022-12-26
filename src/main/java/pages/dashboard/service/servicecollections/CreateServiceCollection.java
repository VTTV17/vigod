package pages.dashboard.service.servicecollections;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

public class CreateServiceCollection {

	final static Logger logger = LogManager.getLogger(CreateServiceCollection.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public CreateServiceCollection(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (id = "collectionName")
    WebElement COLLECTION_NAME;
    
    public CreateServiceCollection inputCollectionName(String name) {
    	commonAction.inputText(COLLECTION_NAME, name);
    	logger.info("Input '" + name + "' into Collection Name field.");
    	return this;
    }
    
}
