package pages.dashboard.service.servicecollections;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
    @FindBy(css = ".collection-form-editor-row-name b")
    List<WebElement> SERVICE_NAME_LIST;
    @FindBy(css = ".btn-remove__row")
    List<WebElement> DELETE_BTN_LIST;
    
    public CreateServiceCollection inputCollectionName(String name) {
    	commonAction.inputText(COLLECTION_NAME, name);
    	logger.info("Input '" + name + "' into Collection Name field.");
    	return this;
    }
    /*------------------Edit---------------*/
    public List<String> getListServiceName(){
        List<String> serviceNameList = new ArrayList<>();
        while (SERVICE_NAME_LIST.size()>0){
            serviceNameList.add(commonAction.getText(SERVICE_NAME_LIST.get(0)));
            commonAction.clickElement(DELETE_BTN_LIST.get(0));
        }
        return serviceNameList;
    }
    public CreateServiceCollection verifyServiceShowInServiceList(String serviceName){
        Assert.assertTrue(getListServiceName().contains(serviceName),serviceName+": not show in collection");
        logger.info("Verify service name display in service list of collection");
        return this;
    }
    public CreateServiceCollection verifyServiceNotShowInServiceList(String serviceName){
        Assert.assertFalse(getListServiceName().contains(serviceName),serviceName+": show in collection");
        logger.info("Verify %s not display in service list of collection".formatted(serviceName));
        return this;
    }
}
