package web.Dashboard.promotion.buyxgety;
import static utilities.links.Links.DOMAIN;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

/**
 * Shared Page Object for Product/Service Discount Page Objects
 * Acts as a Base Object for Product/Service Discount Page Objects
 * Includes properties and functions that its child can inherit from
 */
public class BuyXGetYPage {
	final static Logger logger = LogManager.getLogger(BuyXGetYPage.class);

	public WebDriver driver;

	UICommonAction commons;
	BuyXGetYElement elements;
	HomePage homePage;

	public BuyXGetYPage(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new BuyXGetYElement();
	}

	/**
	 * A temporary function that helps get rid of the annoying try catch block when reading text from property file
	 * @param propertyKey
	 */
	public String translateText(String propertyKey) {
		String translatedText = null;
		try {
			translatedText = PropertiesUtil.getPropertiesValueByDBLang(propertyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translatedText;
	}	

	BuyXGetYPage navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commons.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public BuyXGetYPage navigateToListScreenByURL() {
		navigateByURL(DOMAIN + "/buy-x-get-y");
		for (int i=0; i<10; i++) {
			if (!commons.getElements(elements.loc_btnCreatePromotion).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Waiting for Create Promotion button to appear");
		}
		return this;
	}		

	public BuyXGetYPage navigateToCreateScreenByURL() {
		navigateByURL(DOMAIN + "/buy-x-get-y/create");
		return this;
	}	

	public BuyXGetYPage navigateToDetailScreenByURL(int programId) {
		navigateByURL(DOMAIN + "/buy-x-get-y/detail/" + programId);
		return this;
	}	

	public BuyXGetYPage navigateToEditScreenByURL(int programId) {
		navigateByURL(DOMAIN + "/buy-x-get-y/edit/" + programId);
		return this;
	}	

	public List<List<String>> getPromotionTable() {
		commons.sleepInMiliSecond(1000, "Wait a little till Buy X Get Y list appears"); //Will find a better way to handle this
		List<List<String>> table = new ArrayList<>();
		for (int i = 0; i < commons.getElements(elements.loc_lstPromotionName).size(); i++) {
			List<String> rowData = new ArrayList<>();
			rowData.add(commons.getText(elements.loc_lstPromotionName, i));
			rowData.add(commons.getText(elements.loc_lstGiveawayType, i));
			rowData.add(commons.getText(elements.loc_lstPromotionActiveDate, i));
			rowData.add(commons.getText(elements.loc_lstPromotionStatus, i));
			rowData.add(commons.getText(elements.loc_lstTotalOrder, i));
			table.add(rowData);
		}
		return table;
	}	

	public BuyXGetYPage inputDiscountCodeName(String name) {
		commons.inputText(elements.loc_txtPromotionName, name);
		logger.info("Input discount code name: " + name);
		return this;
	}
	
	public BuyXGetYPage getProgramNameInDetailScreen() {
		String name = "";
		for (int i=0; i<10; i++) {
			name = commons.getText(elements.loc_lblNameField);
			if (!name.isEmpty()) break;
			commons.sleepInMiliSecond(500, "Program name is empty. Retrying to get program name");
		}
		logger.info("Retrieved Program name: " + name);
		return this;
	}

	public BuyXGetYPage selectSegment(int option) {
		commons.click(elements.loc_rdoSegmentOptions, option);
		logger.info("Selected Segment option: " + option);
		return this;
	}	

	public BuyXGetYPage clickAddSegmentLink() {
		commons.click(elements.loc_lnkAddSegment);
		logger.info("Clicked on Add Segment link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectSegment).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Segment dialog to appear");
		}
		return this;
	}		

	public BuyXGetYPage selectApplyToOption(int option) {
		commons.click(elements.loc_rdoApplyToOptions, option);
		logger.info("Selected Apply To option: " + option);
		return this;
	}	

	public BuyXGetYPage selectApplyToAnyItemOption(int option) {
		commons.click(elements.loc_rdoApplyToAnyItemOptions, option);
		logger.info("Selected Apply To Any Items option: " + option);
		return this;
	}

	public BuyXGetYPage clickAddProductsForComboBtn() {
		commons.clickJS(elements.loc_btnAddProductForCombo);
		logger.info("Clicked on Add Product button");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectProduct).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Product dialog to appear");
		}
		return this;
	}		

	public BuyXGetYPage clickAddCollectionLink() {
		commons.click(elements.loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Collection link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectCollection).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Collection dialog to appear");
		}
		return this;
	}		
	public BuyXGetYPage clickAddSpecificProductLink() {
		commons.click(elements.loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Product link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectProduct).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Product dialog to appear");
		}
		return this;
	}

	public BuyXGetYPage selectGiftOption(int option) {
		commons.click(elements.loc_rdoGiftOptions, option);
		logger.info("Selected Gift option: " + option);
		return this;
	}	
	public BuyXGetYPage clickAddCollectionAsGiftLink() {
		commons.click(elements.loc_lnkAddCollectionOrSpecificProductAsGift);
		logger.info("Clicked on Add Collection as gift link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectCollection).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Collection dialog to appear");
		}
		return this;
	}		
	public BuyXGetYPage clickAddSpecificProductAsGiftLink() {
		commons.click(elements.loc_lnkAddCollectionOrSpecificProductAsGift);
		logger.info("Clicked on Add Product as gift link");
		for (int i=0; i<5; i++) {
			if (!commons.getElements(elements.loc_dlgSelectProduct).isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait a little until the Add Product dialog to appear");
		}
		return this;
	}


	public BuyXGetYPage inputSearchTermInDialog(String searchTerm) {
		commons.inputText(elements.loc_txtSearchInDialog, searchTerm);
		logger.info("Input search term: " + searchTerm);
//		commons.sleepInMiliSecond(1000, "Wait a little inputSearchTermInDialog"); //Will find a better way to remove this sleep
		homePage.waitTillSpinnerDisappear();
		return this;
	}		

	public boolean isProductPresentInDialog() {
		commons.sleepInMiliSecond(1000, "Wait a little for products to appear in the Add Product dialog");
		return !commons.getElements(new ByChained(elements.loc_dlgSelectProduct, elements.loc_tblProductNames)).isEmpty();
	}

	public boolean isCollectionPresentInDialog() {
		commons.sleepInMiliSecond(1000, "Wait a little for collections to appear in the Add Collection dialog");
		return !commons.getElements(elements.loc_tblCollectionNames).isEmpty();
	}	    

	public BuyXGetYPage clickSaveBtn() {
		commons.click(elements.loc_btnSave);
		logger.info("Clicked on Save button");
		return this;
	}		

	public BuyXGetYPage clickMarkExpiredBtn() {
		commons.click(elements.loc_btnMarkExpired);
		logger.info("Clicked on 'Mark Expired' button");
		return this;
	}		

	public BuyXGetYPage clickStopBtn(int index) {
		commons.click(elements.loc_btnStop, index);
		logger.info("Clicked on 'Stop' program button at index: " + index);
		return this;
	}		

	public String getPageTitle() {
		String title = commons.getText(elements.loc_lblPageTitle);
		logger.info("Retrieved page title: " + title);
		return title;
	}	

	void checkPermissionToViewBuyXGetYList(AllPermissions staffPermission) {
		navigateToListScreenByURL(); 
		List<List<String>> records = getPromotionTable();

		if (staffPermission.getPromotion().getBxGy().isViewBuyXGetYList()) {
			Assert.assertTrue(!records.isEmpty(), records.toString());
		} else {
			Assert.assertTrue(records.isEmpty(), records.toString());
		}
		logger.info("Finished " + new Object(){}.getClass().getEnclosingMethod().getName());
	}

	void checkPermissionToViewBuyXGetYDetail(AllPermissions staffPermission, int programId) {
		navigateToDetailScreenByURL(programId);

		if (staffPermission.getPromotion().getBxGy().isViewBuyXGetYDetail()) {
			Assert.assertNotEquals(getProgramNameInDetailScreen(), "");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToViewBuyXGetYDetail");
	}

	void checkPermissionToCreateBuyXGetYProgram(AllPermissions staffPermission, String productNotCreatedByStaff, String productCreatedByStaff) {
		navigateToCreateScreenByURL();

		if(staffPermission.getPromotion().getBxGy().isCreateBuyXGetY()) {
			selectSegment(1);
			clickAddSegmentLink();
			if (staffPermission.getCustomer().getSegment().isViewSegmentList()) {
				Assert.assertTrue(!commons.getElements(elements.loc_tblSegmentNames).isEmpty());
			} else {
				Assert.assertTrue(commons.getElements(elements.loc_tblSegmentNames).isEmpty());
			}

			//Combo
			commons.refreshPage();
			selectApplyToOption(0);
			clickAddProductsForComboBtn();
			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
				inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(isProductPresentInDialog());
					inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(isProductPresentInDialog());
				} else {
					Assert.assertFalse(isProductPresentInDialog());
				}
			}   

			//Any items on list + products
			commons.refreshPage();
			selectApplyToOption(1);
			selectApplyToAnyItemOption(0);
			clickAddSpecificProductLink();
			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
				inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(isProductPresentInDialog());
					inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(isProductPresentInDialog());
				} else {
					Assert.assertFalse(isProductPresentInDialog());
				}
			}    

			//Any items on list + collections
			commons.refreshPage();
			selectApplyToOption(1);
			selectApplyToAnyItemOption(1);
			clickAddCollectionLink();
			if (staffPermission.getProduct().getCollection().isViewCollectionList()) {
				Assert.assertTrue(isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(isCollectionPresentInDialog());
			}  		

			//Gift + collections
			commons.refreshPage();
			selectGiftOption(0);
			clickAddCollectionAsGiftLink();
			if (staffPermission.getProduct().getCollection().isViewCollectionList()) {
				Assert.assertTrue(isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(isCollectionPresentInDialog());
			}  		

			//Gift + products
			commons.refreshPage();
			selectGiftOption(1);
			clickAddSpecificProductAsGiftLink();
			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
				inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(isProductPresentInDialog());
					inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(isProductPresentInDialog());
				} else {
					Assert.assertFalse(isProductPresentInDialog());
				}
			} 
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToViewBuyXGetYDetail");
	}

	void checkPermissionToEndBuyXGetYProgram(AllPermissions staffPermission, int programId) {
		if (staffPermission.getPromotion().getBxGy().isViewBuyXGetYDetail()) {
			navigateToDetailScreenByURL(programId);
			clickMarkExpiredBtn();
			if (staffPermission.getPromotion().getBxGy().isEndBuyXGetY()) {
				new ConfirmationDialog(driver).clickOKBtn();
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			logger.info("Skipped ending Buy X Get Y from detail screen");
		}

		if (staffPermission.getPromotion().getBxGy().isViewBuyXGetYList()) {
			navigateToListScreenByURL();
			clickStopBtn(0);
			if (staffPermission.getPromotion().getBxGy().isEndBuyXGetY()) {
				new ConfirmationDialog(driver).clickOKBtn();
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			logger.info("Skipped ending Buy X Get Y from list screen");
		}
		logger.info("Finished checkPermissionToViewBuyXGetYDetail");
	}    

	void checkPermissionToEditBuyXGetYProgram(AllPermissions staffPermission, int programId, String productNotCreatedByStaff, String productCreatedByStaff) {
		navigateToEditScreenByURL(programId);

		if (staffPermission.getPromotion().getBxGy().isViewBuyXGetYDetail()) {
			selectSegment(1);
			clickAddSegmentLink();
			if (staffPermission.getCustomer().getSegment().isViewSegmentList()) {
				Assert.assertTrue(!commons.getElements(elements.loc_tblSegmentNames).isEmpty());
			} else {
				Assert.assertTrue(commons.getElements(elements.loc_tblSegmentNames).isEmpty());
			}

			//Combo
			commons.refreshPage();
			selectApplyToOption(0);
			clickAddProductsForComboBtn();
			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
				inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(isProductPresentInDialog());
					inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(isProductPresentInDialog());
				} else {
					Assert.assertFalse(isProductPresentInDialog());
				}
			}   

			//Any items on list + products
			commons.refreshPage();
			selectApplyToOption(1);
			selectApplyToAnyItemOption(0);
			clickAddSpecificProductLink();
			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
				inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(isProductPresentInDialog());
					inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(isProductPresentInDialog());
				} else {
					Assert.assertFalse(isProductPresentInDialog());
				}
			}    

			//Any items on list + collections
			commons.refreshPage();
			selectApplyToOption(1);
			selectApplyToAnyItemOption(1);
			clickAddCollectionLink();
			if (staffPermission.getProduct().getCollection().isViewCollectionList()) {
				Assert.assertTrue(isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(isCollectionPresentInDialog());
			}  		

			//Gift + collections
			commons.refreshPage();
			selectGiftOption(0);
			clickAddCollectionAsGiftLink();
			if (staffPermission.getProduct().getCollection().isViewCollectionList()) {
				Assert.assertTrue(isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(isCollectionPresentInDialog());
			}  		

			//Gift + products
			commons.refreshPage();
			selectGiftOption(1);
			clickAddSpecificProductAsGiftLink();
			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
				inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(isProductPresentInDialog());
					inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(isProductPresentInDialog());
				} else {
					Assert.assertFalse(isProductPresentInDialog());
				}
			}
			
			commons.refreshPage();
			getPageTitle();
			clickSaveBtn();
			if (staffPermission.getPromotion().getBxGy().isEditBuyXGetY()) {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			//When BH-32911 is resolved, delete the 4 lines below
			if (staffPermission.getPromotion().getBxGy().isEditBuyXGetY()) {
				logger.debug("There's a bug in this scenario, skipping editing operation for now!");
				return;
			}
			
			getPageTitle();
			clickSaveBtn();
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToEditBuyXGetYProgram");
	}	

	public void checkBuyXGetYPermission(AllPermissions staffPermission, int programId, int programIdToEnd, String productNotCreatedByStaff, String productCreatedByStaff) {
//		logger.info("Permissions: " + staffPermission);
		checkPermissionToViewBuyXGetYList(staffPermission);
		checkPermissionToViewBuyXGetYDetail(staffPermission, programId);
		checkPermissionToCreateBuyXGetYProgram(staffPermission, productNotCreatedByStaff, productCreatedByStaff);
		checkPermissionToEndBuyXGetYProgram(staffPermission, programIdToEnd);
		checkPermissionToEditBuyXGetYProgram(staffPermission, programId, productNotCreatedByStaff, productCreatedByStaff);
	}	

}
