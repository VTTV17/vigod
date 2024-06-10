package web.Dashboard.marketing.affiliate.partnerinventory;

import api.Seller.affiliate.partnerinventory.APIPartnerCreateTransfer;
import api.Seller.affiliate.partnerinventory.APIPartnerTransferDetail;
import api.Seller.affiliate.partnerinventory.APITransferList;
import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.atp.Switch;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.enums.TransferStatus;
import utilities.links.Links;
import utilities.model.dashboard.marketing.affiliate.PartnerTransferInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

import java.util.List;

public class TransferPage extends TransferElement{
    final static Logger logger = LogManager.getLogger(TransferPage.class);
    WebDriver driver;
    UICommonAction common;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    AddEditTransferPage addEditTransferPage;
    LoginInformation sellerCredential;
    LoginInformation resellerCredential;
    LoginInformation staffCredential;

    TrackingStockPage trackingStockPage;
    TransferDetailPage transferDetailPage;

    public TransferPage(WebDriver driver) {
        this.driver = driver;
        common = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        addEditTransferPage = new AddEditTransferPage(driver);
        trackingStockPage = new TrackingStockPage(driver);
        transferDetailPage = new TransferDetailPage(driver);
    }
    public TransferPage getLoginInfo(LoginInformation sellerCredential, LoginInformation resellerCredential,LoginInformation staffCredential){
        this.sellerCredential = sellerCredential;
        this.resellerCredential = resellerCredential;
        this.staffCredential = staffCredential;
        return this;
    }
    public AddEditTransferPage clickAddTransferBtn(){
        common.click(loc_transferBtn);
        logger.info("Click on Transfer product to partner button");
        return new AddEditTransferPage(driver);
    }
    public void navigateByUrl(){
        String url = Links.DOMAIN + Links.AFFILIATE_TRANSFER_GOODS_PATH;
        common.navigateToURL(url);
        logger.info("Navigate to url: "+url);
    }
    /*-----------------Check permission-------------------*/
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
    public void checkViewResellerListPermission(){
        addEditTransferPage.clickSelectPartner();
        if(hasViewResellerList()){
            assertCustomize.assertTrue(addEditTransferPage.isPartnerSuggestionShow(),"[Failed] Partner list should be shown");
        }else assertCustomize.assertFalse(addEditTransferPage.isPartnerSuggestionShow(),"[Failed] Partner list should not be shown.");
        logger.info("Verified View reseller list permission.");
    }
    public void checkViewBranchPermission(){
        List<Integer> branchIds = new Login().getInfo(staffCredential).getAssignedBranchesIds();
        List<String> branchNamesAssigned = new BranchManagement(staffCredential).getBranchNameById(branchIds);
        addEditTransferPage.clickBranch();
        List<String> branchListActual = new AddEditTransferPage(driver).getBranchListShow();
        assertCustomize.assertEquals(branchListActual,branchNamesAssigned,
                "[Failed] Branch list expected: %s \nBranch list actual: %s".formatted(branchNamesAssigned,branchListActual));
        logger.info("Verified View Branch list permission.");
    }
    public void checkViewInventorySummary(){
        if(hasViewInventorySummary()){
            trackingStockPage.navigateByUrl();
            assertCustomize.assertTrue(trackingStockPage.isProductInventoryListShow(),
                    "[Failed] Inventory summary (Partner's product inventory) should be shown.");
        }else assertCustomize.assertFalse(trackingStockPage.isProductInventoryListShow(),
                "[Failed] Inventory summary (Partner's product inventory) should not be shown.");
    }
    public void checkCreateTransfer(){
        if(hasCreateTransfer()){
            if(hasViewInventorySummary())
            if((hasViewProductList()||hasViewCreatedProductList())&& hasViewResellerList()){
                clickAddTransferBtn();
                addEditTransferPage.inputInfoToCreateTransferRandom().clickSave();
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.transfer.create.successMessage"),
                            "[Failed] Create success message should be shown, but '%s' is shown".formatted(toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_transferBtn,Links.AFFILIATE_CREATE_TRANSFER_PATH),
                        "[Failed] Create partner page should be shown.");
            }
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_transferBtn),
                "");
        logger.info("Verified View inventory summary permission.");
    }

    /**
     * Check permission of Transfer product to partner button on 2 page: Tracking Stock and Transfer Goods
     */
    public void checkCreateTransferOnTwoPage(){
        navigateByUrl();
        checkCreateTransfer();
        trackingStockPage.navigateByUrl();
        checkCreateTransfer();
    }
    public int getTransferOfStaff(TransferStatus transferStatus){
        List<Integer> branchIds = new Login().getInfo(staffCredential).getAssignedBranchesIds();

        List<Integer> ids = new APITransferList(staffCredential).getTransferByOriginBranch(0, transferStatus);
        if(ids.isEmpty()){
            int newTransferId = new APIPartnerCreateTransfer(sellerCredential,resellerCredential).createPartnerTransfer(branchIds.get(0)).getId();
            ids.add(newTransferId);
            switch (transferStatus){
                case DELIVERING -> new APIPartnerTransferDetail(sellerCredential).shipGoodTransfer(newTransferId);
                // add new case when you need
            }
        }
        return ids.get(0);
    }
    public void checkViewTransferDetail(int transferId){
        String url = Links.DOMAIN + Links.AFFILIATE_TRANSFER_DETAIL_PATH.formatted(transferId);
        if(hasViewTransferDetail()){
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(url,Links.AFFILIATE_TRANSFER_DETAIL_PATH.formatted(transferId)),
                    "[Failed] Transfer detail should be shown, but '%s' is shown".formatted(common.getCurrentURL()));
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),
                "[Failed] Restricted page should be shown when navigate to transfer detail.");
    }
    public void checkEditTransfer(int id){
        String urlEdit = Links.DOMAIN + Links.AFFILIATE_TRANSFER_EDIT_PATH.formatted(id);
        if(hasViewTransferDetail()){
            transferDetailPage.navigateByUrl(id);
            if(hasEditTransfer()){
//                transferDetailPage.clickEditTransfer();
                addEditTransferPage.deleteAllTransferProduct();
                addEditTransferPage.selectProduct();
                addEditTransferPage.clickSave();
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("affiliate.transfer.update.successMessage"),
                            "[Failed] Updated success message should be shown, but '%s' is shown.".formatted(toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(transferDetailPage.loc_lst_actions,0),
                    "[Failed] Restricted popup should be shown when click edit transfer.");
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(urlEdit),
                "[Failed] Restricted page should be shown when navigate to edit transfer page.");
    }
    public void checkCancelTransfer(int id){
        if(hasViewTransferDetail()) {
            transferDetailPage.navigateByUrl(id);
            transferDetailPage.clickSelectAction();
            if (hasCancelTransfer()) {
                transferDetailPage.clickCancelTransfer();
                new ConfirmationDialog(driver).clickOnRedBtn();
                String toasMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toasMessage,PropertiesUtil.getPropertiesValueByDBLang("affiliate.transfer.update.successMessage"),
                            "[Failed] Update success message should be shown when cancel transfer, but '%s' is shown.".formatted(toasMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(transferDetailPage.loc_lst_actions,1),
                        "[Failed] Restricted popup should be shown when click cancel transfer");
            }
        }else logger.info("Don't have View Transfer detail permission, so no need check Cancel transfer permission.");
    }
    public void checkConfirmShipGoods(){
        if(hasViewTransferDetail()){
            int id = getTransferOfStaff(TransferStatus.READY_FOR_TRANSPORT);
            transferDetailPage.navigateByUrl(id);
            if(hasConfirmShipGoods()){
                transferDetailPage.clickTransferShipGoods_ReceiveGood();
                String toasMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toasMessage,PropertiesUtil.getPropertiesValueByDBLang("affiliate.transfer.update.successMessage"),
                            "[Failed] Update success message should be shown when confirm goods, but '%s' is shown.".formatted(toasMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(transferDetailPage.loc_btnShipGoods_ReceiveGood),
                        "[Failed] Restricted popup should be shown when click on Ship Goods button.");
            }
        }else logger.info("Don't have View transfer detail, so no need check Confirm Good permission.");
    }
    public void checkConfirmReceivedGood(){
        if(hasViewTransferDetail()){
            int id = getTransferOfStaff(TransferStatus.DELIVERING);
            transferDetailPage.navigateByUrl(id);
            if(hasConfirmReceivedGood()){
                transferDetailPage.clickTransferShipGoods_ReceiveGood();
                String toasMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toasMessage,PropertiesUtil.getPropertiesValueByDBLang("affiliate.transfer.update.successMessage"),
                            "[Failed] Update success message should be shown when confirm receive goods, but '%s' is shown.".formatted(toasMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(transferDetailPage.loc_btnShipGoods_ReceiveGood),
                        "[Failed] Restricted popup should be shown when click on Receive Goods button.");
            }
        }else logger.info("Don't have View transfer detail, so no need check Receive Good permission.");
    }
    public TransferPage checkPartnerTransferPermision(AllPermissions allPermissions, int newTransferId){
        this.allPermissions = allPermissions;
        checkViewInventorySummary();
        checkCreateTransfer();
        checkViewTransferDetail(newTransferId);
        checkEditTransfer(newTransferId);
        checkCancelTransfer(newTransferId);
        checkConfirmShipGoods();
        checkConfirmReceivedGood();
        AssertCustomize.verifyTest();
        return this;
    }
}
