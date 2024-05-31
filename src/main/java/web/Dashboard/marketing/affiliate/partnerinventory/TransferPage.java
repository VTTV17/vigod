package web.Dashboard.marketing.affiliate.partnerinventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import web.Dashboard.marketing.affiliate.general.AffiliateGeneral;

public class TransferPage extends TransferElement{
    final static Logger logger = LogManager.getLogger(TransferPage.class);
    WebDriver driver;
    UICommonAction common;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    AddEditTransferPage addEditTransferPage;

    public TransferPage(WebDriver driver) {
        this.driver = driver;
        common = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        addEditTransferPage = new AddEditTransferPage(driver);
    }
    public AddEditTransferPage clickAddTransferBtn(){
        common.click(loc_transferBtn);
        logger.info("Click on Transfer product to partner button");
        return new AddEditTransferPage(driver);
    }
    public boolean hasViewInventorySummary(){
        return allPermissions.getAffiliate().getResellerInventory().isViewInventorySummary();
    }
    public boolean hasCreateTransfer(){
        return allPermissions.getAffiliate().getResellerInventory().isCreateTransferToReseller();
    }
    public boolean hasViewTransferDetail(){
        return allPermissions.getAffiliate().getResellerInventory().isViewTransferDetail();
    }
    public boolean hasEditTransfer(){
        return allPermissions.getAffiliate().getResellerInventory().isEditTransfer();
    }
    public boolean hasCancelTransfer(){
        return allPermissions.getAffiliate().getResellerInventory().isCancelTransfer();
    }
    public boolean hasConfirmShipGoods(){
        return allPermissions.getAffiliate().getResellerInventory().isConfirmShipGoods();
    }
    public boolean hasConfirmReceivedGood(){
        return  allPermissions.getAffiliate().getResellerInventory().isConfirmReceivedGoods();
    }
    public boolean hasViewProductList(){
        return allPermissions.getProduct().getProductManagement().isViewProductList();
    }
    public boolean hasViewCreatedProductList(){
        return allPermissions.getProduct().getProductManagement().isViewCreatedProductList();
    }
    public boolean hasViewResellerList(){
        return allPermissions.getAffiliate().getResellerPartner().isViewResellerPartnerList();
    }
    public void checkViewProductList(String productOfShopOwner, String productOfStaff){
        if(hasViewProductList()){
            assertCustomize.assertTrue(addEditTransferPage.isProductShowOnSelectProductList(productOfShopOwner),
                    "[Failed]Product is created by shop owner: '%s' should be shown".formatted(productOfShopOwner));
            assertCustomize.assertTrue(addEditTransferPage.isProductShowOnSelectProductList(productOfStaff),
                    "[Failed]Product is created by staff: '%s' should be shown".formatted(productOfStaff));
        }else if(hasViewCreatedProductList()){
            assertCustomize.assertFalse(addEditTransferPage.isProductShowOnSelectProductList(productOfShopOwner),
                    "[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productOfShopOwner));
            assertCustomize.assertTrue(addEditTransferPage.isProductShowOnSelectProductList(productOfStaff),
                    "[Failed]Product is created by: '%s' should be shown".formatted(productOfStaff));
        }else {
            assertCustomize.assertFalse(addEditTransferPage.isProductShowOnSelectProductList(productOfShopOwner),
                    "[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productOfShopOwner));
            assertCustomize.assertFalse(addEditTransferPage.isProductShowOnSelectProductList(productOfStaff),
                    "[Failed]Product is created by staff: '%s' should not be shown".formatted(productOfStaff));
        }
        logger.info("Verified View product list permission.");
    }
}
