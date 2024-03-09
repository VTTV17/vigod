package web.Dashboard.customers.segments;

import static java.lang.Thread.sleep;
import static utilities.links.Links.DOMAIN;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.customers.segments.createsegment.CreateSegment;
import web.Dashboard.home.HomePage;

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
	
	public Segments navigateByURL() {
		String url = DOMAIN + "/customers/segments/list";
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}
	
	public Segments navigateToCreateSegmentScreenByURL() {
		String url = DOMAIN + "/customers/segments/create";
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}
	
	public Segments navigateToEditSegmentScreenByURL(int segmentId) {
		String url = DOMAIN + "/customers/segments/edit/" + segmentId;
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}

    public List<List<String>> getSegmentTable() {
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < commonAction.getElements(elements.loc_tblSegmentIdColumn).size(); i++) {
            List<String> rowData = new ArrayList<>();
            rowData.add(commonAction.getText(elements.loc_tblSegmentIdColumn, i));
            rowData.add(commonAction.getText(elements.loc_tblSegmentNameColumn, i));
            rowData.add(commonAction.getText(elements.loc_tblQuantityColumn, i));
            table.add(rowData);
        }
        return table;
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

    /**
     * Deletes a segment with a specific id
     * @param segmentId
     */
    public Segments deleteSegment(int segmentId) {
        commonAction.click(By.xpath(elements.loc_icnDelete.formatted(segmentId)));
        logger.info("Click on 'Delete' icon to delete customer segment '%s'.".formatted(segmentId));
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

    boolean permissionToViewSegmentList(AllPermissions staffPermission) {
    	return staffPermission.getCustomer().getSegment().isViewSegmentList();
    }
    boolean isAccessRestrictedPresent() {
    	return new CheckPermission(driver).isAccessRestrictedPresent();
    }
    void checkPermissionToViewSegmentList(AllPermissions staffPermission) {
    	navigateByURL();
    	List<List<String>> segmentTable = getSegmentTable();
    	if (permissionToViewSegmentList(staffPermission)) {
    		Assert.assertTrue(!segmentTable.isEmpty());
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent()); //Bug: Current behavior is not the same as expected in ticket
    	}
    	logger.info("Finished checkPermissionToViewSegmentList");
    }
    
    void checkPermissionToCreateSegment(AllPermissions staffPermission, String productNotCreatedByStaff, String productCreatedByStaff) {
    	navigateToCreateSegmentScreenByURL();
    	//Bug: Current behavior is not the same as expected in ticket
    	if (!permissionToViewSegmentList(staffPermission)) {
    		logger.info("Permissions to view segment customer list is false. Skipping checkPermissionToCreateSegment");
    		return;
    	}
    	
    	if (staffPermission.getCustomer().getSegment().isCreateSegment()) {
    		CreateSegment createSegmentPage = new CreateSegment(driver);
    		
        	String dataGroup = null;
        	try {
        		dataGroup = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.dataGroup.purchasedProduct");
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		createSegmentPage.inputSegmentName("Test Permission " + System.currentTimeMillis());
    		createSegmentPage.selectDataGroupCondition(dataGroup);
    		createSegmentPage.clickSelectProduct();
    		
        	if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
        		createSegmentPage.inputProductSearchTerm(productNotCreatedByStaff);
        		Assert.assertTrue(createSegmentPage.isProductPresentInDialog());
        		createSegmentPage.inputProductSearchTerm(productCreatedByStaff);
        		Assert.assertTrue(createSegmentPage.isProductPresentInDialog());
        	} else {
        		if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
            		createSegmentPage.inputProductSearchTerm(productNotCreatedByStaff);
            		Assert.assertFalse(createSegmentPage.isProductPresentInDialog());
            		createSegmentPage.inputProductSearchTerm(productCreatedByStaff);
            		Assert.assertTrue(createSegmentPage.isProductPresentInDialog());
        		} else {
            		Assert.assertFalse(createSegmentPage.isProductPresentInDialog());
        		}
        	}
    		//Need to actually create one?
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToCreateSegment");
    }
    
    void checkPermissionToEditSegment(AllPermissions staffPermission, int segmentId, String productNotCreatedByStaff, String productCreatedByStaff) {
    	navigateToEditSegmentScreenByURL(segmentId);
    	//Bug: Current behavior is not the same as expected in ticket
    	if (!permissionToViewSegmentList(staffPermission)) {
    		logger.info("Permissions to view segment customer list is false. Skipping checkPermissionToCreateSegment");
    		return;
    	}
    	
    	if (staffPermission.getCustomer().getSegment().isEditSegment()) {
    		CreateSegment createSegmentPage = new CreateSegment(driver);
    		
    		String dataGroup = null;
    		try {
    			dataGroup = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.dataGroup.purchasedProduct");
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		createSegmentPage.inputSegmentName("Test Permission " + System.currentTimeMillis());
    		createSegmentPage.selectDataGroupCondition(dataGroup);
    		createSegmentPage.clickSelectProduct();
    		
    		if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
    			createSegmentPage.inputProductSearchTerm(productNotCreatedByStaff);
    			Assert.assertTrue(createSegmentPage.isProductPresentInDialog());
    			createSegmentPage.inputProductSearchTerm(productCreatedByStaff);
    			Assert.assertTrue(createSegmentPage.isProductPresentInDialog());
    		} else {
    			if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
    				createSegmentPage.inputProductSearchTerm(productNotCreatedByStaff);
    				Assert.assertFalse(createSegmentPage.isProductPresentInDialog());
    				createSegmentPage.inputProductSearchTerm(productCreatedByStaff);
    				Assert.assertTrue(createSegmentPage.isProductPresentInDialog());
    			} else {
    				Assert.assertFalse(createSegmentPage.isProductPresentInDialog());
    			}
    		}
    		//Need to actually create one?
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToEditSegment");
    }

    void checkPermissionToDeleteSegment(AllPermissions staffPermission, int segmentId) {
    	navigateByURL();
    	
    	//Bug: Current behavior is not the same as expected in ticket
    	if (!permissionToViewSegmentList(staffPermission)) {
    		logger.info("Permissions to view segment customer list is false. Skipping checkPermissionToCreateSegment");
    		return;
    	}
    	
    	deleteSegment(segmentId);
    	clickOKBtn();
    	
    	if (staffPermission.getCustomer().getSegment().isDeleteSegment()) {
    		new HomePage(driver).getToastMessage();
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToDeleteSegment");
    }    
    
    //https://mediastep.atlassian.net/browse/BH-24817
    public void checkCustomerSegmentPermission(AllPermissions staffPermission, int segmentDesinatedForEditing, int segmentDesignatedForDeletion, String productNotCreatedByStaff, String productCreatedByStaff) {
    	checkPermissionToViewSegmentList(staffPermission);
    	checkPermissionToCreateSegment(staffPermission, productNotCreatedByStaff, productCreatedByStaff);
    	checkPermissionToEditSegment(staffPermission, segmentDesinatedForEditing, productNotCreatedByStaff, productCreatedByStaff);
    	checkPermissionToDeleteSegment(staffPermission, segmentDesignatedForDeletion);
    }       
}
