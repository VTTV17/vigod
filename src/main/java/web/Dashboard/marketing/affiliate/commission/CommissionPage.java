package web.Dashboard.marketing.affiliate.commission;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.Home.Home;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;


import java.util.List;

public class CommissionPage extends CommissionElement{
    final static Logger logger = LogManager.getLogger(CommissionPage.class);
    WebDriver driver;
    UICommonAction common;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    CreateCommissionPage createCommissionPage;
    public CommissionPage(WebDriver driver){
        this.driver = driver;
        common = new UICommonAction(driver);
        createCommissionPage = new CreateCommissionPage(driver);
        assertCustomize = new AssertCustomize(driver);
    }
    public boolean hasViewProductCommissionList(){
        return allPermissions.getAffiliate().getCommission().isViewProductCommissionList();
    }
    public boolean hasAddCommission(){
        return allPermissions.getAffiliate().getCommission().isAddCommission();
    }
    public boolean hasEditCommission(){
        return allPermissions.getAffiliate().getCommission().isEditCommission();
    }
    public boolean hasDeleteCommission(){
        return allPermissions.getAffiliate().getCommission().isDeleteCommission();
    }
    public boolean hasViewProductList(){
        return allPermissions.getProduct().getProductManagement().isViewProductList();
    }
    public boolean hasViewCreatedProduct(){
        return allPermissions.getProduct().getProductManagement().isCreateProduct();
    }
    public boolean hasViewProductCollectionList(){
        return allPermissions.getProduct().getCollection().isViewCollectionList();
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + "/affiliate/commission";
        common.navigateToURL(url);
        logger.info("Navigate to url: "+url);
        common.sleepInMiliSecond(500);
        new HomePage(driver).waitTillSpinnerDisappear1();
    }
    public void clickOnAddCommissionBtn(){
        common.click(loc_btnAddCommission);
        logger.info("CLick on Add commission button.");
    }
    public void verifyViewProductCommissionList(){
        navigateByUrl();
        List<WebElement> commissionNameList = common.getElements(log_lstCommissionName,3);
        if(hasViewProductCommissionList()){
            assertCustomize.assertTrue(commissionNameList.size()>0,"[Failed] Commission list should be shown");
        }else assertCustomize.assertTrue(commissionNameList.isEmpty(),"[Failed] Commission list should be empty, but it show %s commission.".formatted(commissionNameList.size()));
        logger.info("Verified View product commisison list permission.");
    }
    public void checkViewProductList(String productNameOfShopOwner, String productNameOfStaff){
        createCommissionPage.clickOnSpecificProduct()
                .clickOnAddProductLink();
        if(hasViewProductList()){
            assertCustomize.assertTrue(createCommissionPage.isProductShowOnSelectProductList(productNameOfShopOwner),
                    "[Failed]Product is created by shop owner: '%s' should be shown".formatted(productNameOfShopOwner));
            assertCustomize.assertTrue(createCommissionPage.isProductShowOnSelectProductList(productNameOfStaff),
                    "[Failed]Product is created by staff: '%s' should be shown".formatted(productNameOfStaff));
        }else if(hasViewCreatedProduct()){
            assertCustomize.assertFalse(createCommissionPage.isProductShowOnSelectProductList(productNameOfShopOwner),
                    "[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
            assertCustomize.assertTrue(createCommissionPage.isProductShowOnSelectProductList(productNameOfStaff),
                    "[Failed]Product is created by: '%s' should be shown".formatted(productNameOfStaff));
        }else {
            assertCustomize.assertFalse(createCommissionPage.isProductShowOnSelectProductList(productNameOfShopOwner),
                    "[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
            assertCustomize.assertFalse(createCommissionPage.isProductShowOnSelectProductList(productNameOfStaff),
                    "[Failed]Product is created by staff: '%s' should not be shown".formatted(productNameOfStaff));
        }
        logger.info("Verified View product list permission.");
    }
    public void checkViewProductCollectionList(){
        new CreateCommissionPage(driver)
                .clickOnSpecificCollections()
                .clickOnAddCollectionsLink();
        List<WebElement> collectionList = common.getElements(createCommissionPage.loc_lst_lblCollectionName,3);
        if(hasViewProductCollectionList())
            assertCustomize.assertTrue(collectionList.size()>0,"[Failed]Product collection list should be shown");
        else
            assertCustomize.assertTrue(collectionList.isEmpty(),"[Failed]Product collection list should be empty.");
        logger.info("Verified View product collection list permission.");
    }
    public void verifyAddCommission(String productNameOfShowOwner, String productNameOfStaff){
        navigateByUrl();
        if(hasAddCommission()){
            clickOnAddCommissionBtn();
            checkViewProductList(productNameOfShowOwner,productNameOfStaff);
            createCommissionPage.navigateByUrl();
            checkViewProductCollectionList();
            createCommissionPage.navigateByUrl().createSimpleCommission();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.commission.create.successMessage"),
                        "[Failed] Create success message should be shown, but '%s' is shown".formatted(toastMessage));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnAddCommission),
                "[Failed] Restricted popup should be shown when click on Add Commission button.");
        logger.info("Verified Add commission permission.");
    }
    public void verifyEditCommission(){
        navigateByUrl();
        if(hasViewProductCommissionList()){
            common.getElements(loc_lst_icnEdit,3);
            if(hasEditCommission()){
                common.click(loc_lst_icnEdit,0);
                common.sleepInMiliSecond(500);
                common.click(createCommissionPage.loc_btnSave);
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("affiliate.commission.update.successMessage"),
                            "[Failed] Update success message should be shown, but '%s' is shown".formatted(toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),
                    "[Failed] Restricted popup should be shown when click on edit icon.");
        }else logger.info("Don't have view commission list, so no need check edit commission permission.");
        logger.info("Verified Edit commission permission.");

    }
    public void verifyDeleteCommission(){
        if(hasViewProductCommissionList()){
            navigateByUrl();
            common.getElements(loc_lst_icnEdit,3);
            common.click(loc_lst_icnDelete,0);
            if(hasDeleteCommission()){
                new ConfirmationDialog(driver).clickOKBtn();
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("affiliate.commission.delete.successMessage"),
                            "[Failed] Delete success message should be shown, but '%s' is shown.".formatted(toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(new ConfirmationDialog(driver).loc_btnOK,0),
                    "[Failed] Restricted popup should be shown when click on OK button to Delete.");
            logger.info("Verified Delete commission permission");
        }else logger.info("Don't have View commission list, so no need check Delete commission permission.");
    }
    public CommissionPage completeVerifyStaffPermissionCommissionPage() {
//        logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
//        if (assertCustomize.getCountFalse() > 0) {
//            Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
//        }
        AssertCustomize.verifyTest();
        return this;
    }
    public void verifyCommissionPagePermission(AllPermissions allPermissions, String productOfSeller, String productOfStaff){
        this.allPermissions = allPermissions;
        verifyViewProductCommissionList();
        verifyAddCommission(productOfSeller,productOfStaff);
        verifyEditCommission();
        verifyDeleteCommission();
        completeVerifyStaffPermissionCommissionPage();
    }
}
