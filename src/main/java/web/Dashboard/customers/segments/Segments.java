package web.Dashboard.customers.segments;

import static java.lang.Thread.sleep;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.customers.segments.createsegment.CreateSegment;
import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class Segments {

    final static Logger logger = LogManager.getLogger(Segments.class);

    WebDriver driver;
    UICommonAction commonAction;
    SegmentElement elements;
    HomePage homePage;

    public Segments(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        homePage = new HomePage(driver);
        elements = new SegmentElement();
    }

    public Segments navigate() {
        homePage.navigateToPage("Customers", "Segments");
        return this;
    }

    public CreateSegment clickCreateSegmentBtn() {
        commonAction.click(elements.loc_btnCreateSegment);
        logger.info("Clicked on 'Create Segment' button.");
        return new CreateSegment(driver);
    }

    public Segments inputSearchTerm(String customerSegment) {
        commonAction.sendKeys(elements.loc_txtSearchSegment, customerSegment);
        logger.info("Input '" + customerSegment + "' into Search box.");
        return this;
    }

    public Segments deleteSegment(String customerSegment) {
        commonAction.click(By.xpath(elements.loc_btnDelete.formatted(customerSegment)));
        logger.info("Click on 'Delete' icon to delete customer segment '%s'.".formatted(customerSegment));
        return this;
    }

    public Segments clickOKBtn() {
        new ConfirmationDialog(driver).clickGreenBtn();
        logger.info("Clicked on 'OK' button to confirm customer segment deletion.");
        return this;
    }

    public Segments clickCancelBtn() {
    	new ConfirmationDialog(driver).clickGrayBtn();
        logger.info("Clicked on 'Cancel' button to abort customer segment deletion.");
        return this;
    }

    public void openSegmentCustomerPage(String segmentName) throws InterruptedException {
        // search segment
        inputSearchTerm(segmentName);

        // wait api return result
        sleep(1000);

        // click on view icon
        commonAction.click(elements.loc_btnViewIcon);
    }
    
    public void verifyPermissionToCreateSegmentByCustomerData(String dataCondition, String permission) {
    	String displayLanguage = homePage.getDashboardLanguage();
    	String dataGroup = null;
    	try {
    		dataGroup = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.dataGroup.customerData", displayLanguage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if (permission.contentEquals("S")) {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    		return;
    	}
    	
		String selectedption = clickCreateSegmentBtn().inputSegmentName("Test Permission")
		.selectDataGroupCondition(dataGroup)
		.selectDataCondition(dataCondition)
		.getSelectedtDataCondition();
		commonAction.navigateBack();
		new ConfirmationDialog(driver).clickOKBtn();
    	
    	if (permission.contentEquals("A")) {
    		Assert.assertEquals(selectedption, dataCondition);
    	} else {
    		Assert.assertNotEquals(selectedption, dataCondition);
    	}
    }    
    public void verifyPermissionToCreateSegmentByOrderData(String dataCondition, String permission) {
    	String displayLanguage = homePage.getDashboardLanguage();
    	String dataGroup = null;
    	try {
    		dataGroup = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.dataGroup.orderData", displayLanguage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if (permission.contentEquals("A")) {
    		clickCreateSegmentBtn().inputSegmentName("Test Permission")
    		.selectDataGroupCondition(dataGroup)
    		.selectDataCondition(dataCondition);
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		// Not done
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }    
    public void verifyPermissionToCreateSegmentByPurchasedProduct(String permission) {
    	String displayLanguage = homePage.getDashboardLanguage();
    	String dataGroup = null;
    	try {
    		dataGroup = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.dataGroup.purchasedProduct", displayLanguage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if (permission.contentEquals("A")) {
    		clickCreateSegmentBtn().inputSegmentName("Test Permission")
    		.selectDataGroupCondition(dataGroup);
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		// Not done
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }    
}
