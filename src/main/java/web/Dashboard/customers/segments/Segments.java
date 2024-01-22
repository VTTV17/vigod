package web.Dashboard.customers.segments;

import static java.lang.Thread.sleep;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class Segments {

    final static Logger logger = LogManager.getLogger(Segments.class);

    WebDriver driver;
    UICommonAction commonAction;

    public Segments(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_btnCreateSegment = By.cssSelector(".segment-list .btn-create");
    By loc_txtSearchSegment = By.cssSelector(".gs-search-box__wrapper .uik-input__input");
    By loc_btnViewIcon = By.cssSelector("tbody > tr:nth-child(1) > td:nth-child(4) > div > a:nth-child(1)");

    public Segments navigate() {
        new HomePage(driver).navigateToPage("Customers", "Segments");
        return this;
    }

    public CreateSegment clickCreateSegmentBtn() {
        commonAction.click(loc_btnCreateSegment);
        logger.info("Clicked on 'Create Segment' button.");
        return new CreateSegment(driver);
    }

    public Segments inputSearchTerm(String customerSegment) {
        commonAction.sendKeys(loc_txtSearchSegment, customerSegment);
        logger.info("Input '" + customerSegment + "' into Search box.");
        return this;
    }

    public Segments deleteSegment(String customerSegment) {
        String segmentXpath = "//div[contains(@class,'segment-list__widget-body')]//tbody/tr[1]/td[position()=2 and text()='%s']"
                .formatted(customerSegment);
        String deleteBtnXpath = segmentXpath.concat("/following-sibling::*//i[contains(@style,'icon-delete')]");
        commonAction.click(By.xpath(deleteBtnXpath));
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
        commonAction.click(loc_btnViewIcon);
    }
    
    public void verifyPermissionToCreateSegmentByCustomerData(String dataCondition, String permission) {
    	String displayLanguage = new HomePage(driver).getDashboardLanguage();
    	String dataGroup = null;
    	try {
    		dataGroup = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.dataGroup.customerData", displayLanguage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	if (permission.contentEquals("S")) {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    	String displayLanguage = new HomePage(driver).getDashboardLanguage();
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    public void verifyPermissionToCreateSegmentByPurchasedProduct(String permission) {
    	String displayLanguage = new HomePage(driver).getDashboardLanguage();
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
}
