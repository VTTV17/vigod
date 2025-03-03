package web.Dashboard.products.transfer.crud;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.products.all_products.APISuggestionProduct;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.transfer.APITransferDetail;
import api.Seller.products.transfer.TransferManagement;
import api.Seller.setting.BranchManagement;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.Assert;
import sql.SQLGetInventoryEvent;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.EventAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.all_products.crud.sync_lazada.SyncLazadaPage;
import web.Dashboard.products.transfer.management.TransferManagementPage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static api.Seller.products.transfer.TransferManagement.TransferInfo;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.DOMAIN;

public class TransferPage extends TransferElement {
    WebDriver driver;
    UICommonAction commons;

    final static Logger logger = LogManager.getLogger(TransferPage.class);
    LoginInformation loginInformation;
    public TransferPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
        transferManagementPage = new TransferManagementPage(driver);
    }
    public TransferPage(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    }

    HomePage homePage;
    TransferManagementPage transferManagementPage;

    public TransferPage navigate() {
        homePage = new HomePage(driver);
        homePage.navigateToPage("Products", "Transfer");
        commons.waitVisibilityOfElementLocated(transferManagementPage.getLoc_btnAddTransfer());
        return this;
    }

    public void waitTillPageStable() {
        commons.waitVisibilityOfElementLocated(transferManagementPage.getLoc_btnAddTransfer());
        homePage.waitTillLoadingDotsDisappear().waitTillSpinnerDisappear1();
    }

    public TransferPage clickAddTransferBtn() {
        try {
            commons.click(transferManagementPage.getLoc_btnAddTransfer());
        } catch (TimeoutException ex) {
            driver.navigate().refresh();
            commons.click(transferManagementPage.getLoc_btnAddTransfer());
        }
        logger.info("Clicked on 'Add Transfer' button.");
        return this;
    }

    public TransferPage inputSearchTerm(String searchTerm) {
        commons.sendKeys(loc_txtSearchRecord, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search box.");
        return this;
    }

    public TransferPage inputProductSearchTerm(String searchTerm) {
        commons.sendKeys(loc_txtSearchProduct, searchTerm);
        commons.click(loc_txtSearchProduct);
        logger.info("Input '" + searchTerm + "' into Product Search box.");
        commons.sleepInMiliSecond(500); //There's a delay of 500ms before search operation commences
        commons.waitInvisibilityOfElementLocated(searchLoadingIcon);
        return this;
    }

    public TransferPage selectProduct(String name) {
        By productXpath = By.xpath(PRODUCT_NAME_IN_RESULT.formatted("and text()=\"%s\"".formatted(name)));
        commons.click(productXpath);
        logger.info("Selected product: " + name);
        return this;
    }

    public TransferPage selectProductByBarcode(String barcode) {
        commons.click(By.xpath(productBarcodeInResult.formatted(barcode)));
        logger.info("Selected product by barcode: " + barcode);
        return this;
    }

    public TransferPage selectSourceBranch(String name) {
        commons.click(loc_ddlBranches, 0);
        By branchName = By.xpath(str_branch.formatted(name));
        commons.click(branchName);
        logger.info("Selected source branch: " + name);
        return this;
    }

    public void getSearchResults() {
        List<List<String>> resultList = new ArrayList<>();

        for (int i = 0; i < commons.getElements(results).size(); i++) {
            List<String> temp = new ArrayList<>();

            // Get name
            temp.add(commons.getElements(results).get(i).findElement(name).getText());

            // Get barcode
            temp.add(commons.getElements(results).get(i).findElement(barcode).getText());

            // Get variation value if the product has variations
            if (!commons.getElements(results).get(i).findElements(variation).isEmpty()) {
                temp.add(commons.getElements(results).get(i).findElement(variation).getText());
            } else {
                temp.add("");
            }

            // Get inventory
            temp.add(commons.getElements(results).get(i).findElement(inventory).getText());

            // Get conversion units if the product has conversion units
            if (!commons.getElements(results).get(i).findElements(unit).isEmpty()) {
                temp.add(commons.getElements(results).get(i).findElement(unit).getText());
            } else {
                temp.add("");
            }

            resultList.add(temp);
        }
    }

    public TransferPage selectDestinationBranch(String name) {
        commons.click(loc_ddlBranches, 1);
        commons.click(By.xpath(branchName.formatted(name)));
        logger.info("Selected destination branch: " + name);
        return this;
    }

    public TransferPage inputTransferredQuantity(int quantity) {
        commons.sendKeys(loc_txtTransferredQuantity, String.valueOf(quantity));
        logger.info("Input '" + quantity + "' into Transferred Quantity field.");
        return this;
    }

    public void selectIMEI(String imei) {
        By imeiLocator = By.xpath(str_imeiLocator.formatted(imei));
        commons.click(imeiLocator);
        logger.info("Selected IMEI: " + imei);
    }

    public TransferPage selectIMEI(String[] imei) {
        commons.click(loc_lnkSelectIMEI);

        for (String value : imei) {
            selectIMEI(value);
        }
        new ConfirmationDialog(driver).clickOKBtn();
        return this;
    }

    public TransferPage inputNote(String note) {
        commons.sendKeys(loc_txtNote, note);
        logger.info("Input '" + note + "' into Note field.");
        return this;
    }

    public TransferPage clickSaveBtn() {
        commons.click(loc_btnSave);
        logger.info("Clicked on 'Save' button to add a product transfer.");
        return this;
    }

    public void clickShipGoodsBtn() {
        commons.click(loc_btnShipGoods);
        logger.info("Clicked on 'Ship Goods' or 'Receive Goods' button.");
        homePage.waitTillSpinnerDisappear1();
    }

    public void clickReceiveGoodsBtn() {
        clickShipGoodsBtn();
    }

    public TransferPage clickRecord(int recordID) {
        By record = By.cssSelector(str_record.formatted(recordID));
        commons.click(record);
        logger.info("Clicked on transfer record '%s'.".formatted(recordID));
        homePage.waitTillSpinnerDisappear1();
        return this;
    }

    public List<String> getSpecificRecord(int index) {
        //Wait until records are present
        for (int i = 0; i < 6; i++) {
            if (!commons.getElements(loc_tmpRecord).isEmpty()) break;
            commons.sleepInMiliSecond(500);
        }

        /*
         * Loop through the columns of the specific record
         * and store data of the column into an array.
         * Retry the process when StaleElementReferenceException occurs
         */
        try {
            List<String> rowData = new ArrayList<>();
            for (WebElement column : commons.getElement(loc_tmpRecord, index).findElements(By.xpath("./td"))) {
                rowData.add(column.getText());
            }
            return rowData;
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
            List<String> rowData = new ArrayList<>();
            for (WebElement column : commons.getElement(loc_tmpRecord, index).findElements(By.xpath("./td"))) {
                rowData.add(column.getText());
            }
            return rowData;
        }
    }

    public List<List<String>> getRecords() {
        waitTillPageStable();
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < commons.getElements(loc_tmpRecord).size(); i++) {
            table.add(getSpecificRecord(i));
        }
        return table;
    }


    /*Verify permission for certain feature*/
    public void verifyPermissionToTransferProduct(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commons.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commons.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
        }
    }

    int getOriginBranchId(APISuggestionProduct suggestionProduct, List<Integer> assignedBranches) {
        return assignedBranches.stream()
                .filter(assignedBranch -> !suggestionProduct.getAllSuggestProductIdInStock(assignedBranch).getItemIds().isEmpty())
                .findFirst()
                .orElse(0);
    }

    public void navigateToCreateTransferPage() {
        // navigate to transfer management page
        driver.get("%s/product/transfer/list".formatted(DOMAIN));

        // navigate to create transfer page
        clickAddTransferBtn();

        logger.info("Navigate to create transfer page.");
    }

    private void navigateToEditTransferPage(int transferId) {
        // navigate to edit transfer page by URL
        if (!driver.getCurrentUrl().contains("/product/transfer/edit/%s".formatted(transferId)))
            driver.get("%s/product/transfer/edit/%s".formatted(DOMAIN, transferId));
        logger.info("Navigate to edit transfer page by URL, transferId: %s.".formatted(transferId));
    }

    public void navigateToTransferDetailPage(int transferId) {
        // navigate to transfer detail page by URL
        if (!driver.getCurrentUrl().contains("/product/transfer/wizard/%s".formatted(transferId)))
            driver.get("%s/product/transfer/wizard/%s".formatted(DOMAIN, transferId));
        logger.info("Navigate to transfer detail page by URL, transferId: %s.".formatted(transferId));
    }

    public void removeOldProducts() {
        List<WebElement> removeIcons = commons.getListElement(loc_icnRemove);
        if (!removeIcons.isEmpty()) {
            IntStream.iterate(removeIcons.size() - 1, index -> index >= 0, index -> index - 1)
                    .forEach(index -> commons.clickJS(loc_icnRemove, index));
            logger.info("Remove old products.");
        }
    }

    private void inputTransferInfo(LoginInformation loginInformation) {
        // get login information
        LoginDashboardInfo loginInfo = new Login().getInfo(loginInformation);

        // init branch management API
        BranchManagement branchManagement = new BranchManagement(loginInformation);
        // get assigned branches
        List<Integer> assignedBranchIds = loginInfo.getAssignedBranchesIds();

        // get destination branches
        BranchInfo destinationInfo = branchManagement.getDestinationBranchesInfo();
        List<String> destinationBranchNames = destinationInfo.getBranchName();

        // init get all products API
        APISuggestionProduct suggestionProduct = new APISuggestionProduct(loginInformation);

        // find origin branch that have in-stock product
        int originBranchId = getOriginBranchId(suggestionProduct, assignedBranchIds);

        // create transfer
        if (originBranchId != 0) {
            if (destinationBranchNames.size() > 1) {
                // index of origin branches
                int index = destinationInfo.getBranchID().indexOf(originBranchId);

                // get origin branch name
                String originBranchName = destinationBranchNames.get(index);
                logger.info("Found in-stock origin branch: %s.".formatted(originBranchName));

                // remove origin branch from destination branches
                destinationBranchNames.remove(originBranchName);

                // get destination branch
                String destinationBranch = destinationBranchNames.get(nextInt(destinationBranchNames.size()));
                logger.info("Get destination branch: %s.".formatted(destinationBranch));

                // get transfer product
                APISuggestionProduct.AllSuggestionProductsInfo info = suggestionProduct.getAllSuggestProductIdInStock(originBranchId);
                String manageTypes = info.getInventoryManageTypes().get(0);
                String itemName = info.getItemNames().get(0);
                int itemId = info.getItemIds().get(0);
                int modelId = info.getModelIds().get(0);
                String barcode = modelId == 0 ? "%s".formatted(itemId) : "%s - %s".formatted(itemId, modelId);
                long transferredQuantity = nextLong(info.getRemainingStocks().get(0)) + 1;

                // create transfer
                this.selectSourceBranch(originBranchName)
                        .selectDestinationBranch(destinationBranch)
                        .inputProductSearchTerm(itemName)
                        .selectProductByBarcode(barcode)
                        .inputTransferredQuantity((int) transferredQuantity)
                        .inputNote("Transfer product from branch '%s' to branch '%s'"
                                .formatted(originBranchName, destinationBranch));

                if (manageTypes.equals("IMEI_SERIAL_NUMBER")) {
                    List<String> listIMEI = new APIProductDetail(loginInformation).getListIMEI(itemId, modelId, originBranchId);
                    this.selectIMEI(listIMEI.subList(0, (int) transferredQuantity).toArray(new String[0]));
                }
            } else logger.warn("Must have at least 2 branches to create a new transfer.");
        } else logger.warn("All products are out of stock, so can not create a new transfer.");
    }

    public void createTransfer() {
        // navigate to create transfer page
        navigateToCreateTransferPage();

        // input transfer info
        inputTransferInfo(staffLoginInformation);

        // complete create transfer
        clickSaveBtn();
    }

    public void editTransfer(int transferId) {
        // navigate to update transfer page
        navigateToEditTransferPage(transferId);

        // remove old products
        removeOldProducts();

        // input transfer info
        inputTransferInfo(staffLoginInformation);

        // complete create transfer
        clickSaveBtn();
    }

    /*-------------------------------------*/
    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-31079
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    List<Integer> assignedBranchIds;
    TransferManagement transferManagement;
    LoginInformation sellerLoginInformation;
    LoginInformation staffLoginInformation;

    public TransferPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        return this;
    }

    public void checkViewTransferDetail(AllPermissions permissions, List<Integer> assignedBranchIds) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init assert customize
        assertCustomize = new AssertCustomize(driver);

        // get assigned branchesIds
        this.assignedBranchIds = assignedBranchIds;

        // get transfer info with seller token
        TransferInfo transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();

        // init transfer management API
        this.transferManagement = new TransferManagement(staffLoginInformation);

        // check permission
        int hasViewPermissionTransferId = transferManagement.getViewPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
        if (permissions.getProduct().getTransfer().isViewTransferDetail()) {
            // check can access to transfer detail page

            if (hasViewPermissionTransferId != 0) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/product/transfer/wizard/%s"
                                .formatted(DOMAIN, hasViewPermissionTransferId), String.valueOf(hasViewPermissionTransferId)),
                        "Transfer detail page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
            } else logger.info("Can not find any transfer with origin/destination branch in assigned branch.");

            // Staff have permission view transfer detail
            // but assigned branch NOT contains original or destination of the transfer
            // => show restricted page
            int noViewPermissionTransferId = transferManagement.getNoViewPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
            if (noViewPermissionTransferId != 0) {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/product/transfer/wizard/%s"
                                .formatted(DOMAIN, noViewPermissionTransferId)),
                        "Restricted page is not shown.");
            }

            // check edit transfer
            checkEditTransfer();

            // check confirm ship goods
            checkConfirmShipGoods();

            // check received goods
            checkConfirmReceivedGoods();

            // check cancel transfer
            checkCancelTransfer();
        } else {
            // Staff don’t have permission “View transfer detail”
            // => show restricted page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/product/transfer/wizard/%s"
                            .formatted(DOMAIN, hasViewPermissionTransferId)),
                    "Restricted page is not shown.");
        }

        // log
        logger.info("Check permission: Product >> Transfer >> View transfer details.");
    }

    void checkEditTransfer() {
        // check edit actions
        // get transfer info with seller token
        TransferInfo transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();
        int hasEditPermissionTransferId = transferManagement.getConfirmShipGoodsPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
        if (permissions.getProduct().getTransfer().isEditTransfer()) {
            // check can access to edit transfer page
            if (hasEditPermissionTransferId != 0) {
                // navigate to transfer detail page
                navigateToTransferDetailPage(hasEditPermissionTransferId);

                // open list actions
                commons.clickJS(loc_lnkSelectAction);
                commons.clickJS(loc_ddlListActions, 0);

                // check can navigate to edit transfer page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/product/transfer/edit/%s"
                                .formatted(DOMAIN, hasEditPermissionTransferId), String.valueOf(hasEditPermissionTransferId)),
                        "Edit transfer page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
                try {
                    editTransfer(hasEditPermissionTransferId);
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        } else if (hasEditPermissionTransferId != 0) {
            // navigate to transfer detail page
            navigateToTransferDetailPage(hasEditPermissionTransferId);

            // open list actions
            commons.clickJS(loc_lnkSelectAction);

            // Staff don’t have permission Edit transfer but Click [Edit] function in a transfer detail
            // => Show restricted popup
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 0),
                    "Restricted popup is not shown.");
        }
        logger.info("Check permission hen accessing transfer page editing through transfer details page.");

        // check edit permission at edit transfer page
        if (permissions.getProduct().getTransfer().isEditTransfer()) {
            if (hasEditPermissionTransferId != 0) {
                // check can access to edit transfer page by URL
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/product/transfer/edit/%s"
                                .formatted(DOMAIN, hasEditPermissionTransferId), String.valueOf(hasEditPermissionTransferId)),
                        "Edit transfer page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
            }
            // get current transfer info with seller token
            transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();
            int noEditPermissionTransferId = transferManagement.getNoConfirmShipGoodsPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
            if (noEditPermissionTransferId != 0) {
                // navigate to edit transfer page by url
                navigateToEditTransferPage(noEditPermissionTransferId);

                // re-input stock to enable Save button
                int currentStock = Integer.parseInt(commons.getValue(loc_txtTransferredQuantity)) + 1;
                commons.sendKeys(loc_txtTransferredQuantity, String.valueOf(currentStock));
                commons.click(loc_txtSearchProduct);

                // Staff have permission Edit transfer but assigned branch not contains Original branch of transfer
                // => Click [Save] in Transfer edit mode => Show restricted popup
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave),
                        "Restricted popup is not shown.");
            }
        } else if (hasEditPermissionTransferId != 0) {
            // Staff don’t have permission Edit transfer but Open direct edit URL of a transfer
            // => Show restricted page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/product/transfer/edit/%s"
                            .formatted(DOMAIN, hasEditPermissionTransferId)),
                    "Restricted page is not shown.");
        }
        logger.info("Check permission when access by url.");

        // log
        logger.info("Check permission: Product >> Transfer >> Edit transfer.");
    }

    void checkConfirmShipGoods() {
        // get transfer info with seller token
        TransferInfo transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();

        // check permission
        int hasConfirmShipGoodsPermissionTransferId = transferManagement.getConfirmShipGoodsPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
        if (permissions.getProduct().getTransfer().isConfirmShipGoods()) {
            if (hasConfirmShipGoodsPermissionTransferId != 0) {
                // navigate to transfer detail page
                navigateToTransferDetailPage(hasConfirmShipGoodsPermissionTransferId);

                // check can ship goods
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnShipGoods, loc_dlgToastSuccess),
                        "Can not ship goods.");

            }

            // get current transfer info
            transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();
            int noConfirmShipGoodsPermissionTransferId = transferManagement.getNoConfirmShipGoodsPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
            if (noConfirmShipGoodsPermissionTransferId != 0) {
                // Staff Have permission Confirm ship goods
                // but Assigned branches NOT contains original branch in transfer
                // and Click [Ship Goods] button in a transfer detail
                // => Show restricted popup
                navigateToTransferDetailPage(noConfirmShipGoodsPermissionTransferId);
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnShipGoods),
                        "Restricted popup is not shown.");
            }
        } else if (hasConfirmShipGoodsPermissionTransferId != 0) {
            // navigate to transfer detail page
            navigateToTransferDetailPage(hasConfirmShipGoodsPermissionTransferId);

            // Staff don’t have permission Confirm Ship Goods
            // but Click [Ship Goods] button in a transfer detail
            // => show restricted popup
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnShipGoods),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Product >> Transfer >> Confirm ship goods.");
    }

    void checkConfirmReceivedGoods() {
        // get transfer info with seller token
        TransferInfo transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();

        // check permission
        int hasConfirmReceivedGoodsPermissionTransferId = transferManagement.getConfirmReceiveGoodsPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
        if (permissions.getProduct().getTransfer().isConfirmReceivedGoods()) {
            if (hasConfirmReceivedGoodsPermissionTransferId != 0) {
                // navigate to transfer detail page
                navigateToTransferDetailPage(hasConfirmReceivedGoodsPermissionTransferId);

                // check can receive goods
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnReceiveGoods, loc_dlgToastSuccess),
                        "Can not receive goods.");
            }

            // get current transfer info with seller token
            transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();
            int noConfirmReceivedGoodsPermissionTransferId = transferManagement.getNoConfirmReceiveGoodsPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
            if (noConfirmReceivedGoodsPermissionTransferId != 0) {
                // Staff Have permission Confirm received goods
                // but Assigned branches NOT contains destination branch in transfer
                // and Click [Ship Goods] button in a transfer detail
                // => Show restricted popup
                navigateToTransferDetailPage(noConfirmReceivedGoodsPermissionTransferId);
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnReceiveGoods),
                        "Restricted popup is not shown.");
            }
        } else if (hasConfirmReceivedGoodsPermissionTransferId != 0) {
            // navigate to transfer detail page
            navigateToTransferDetailPage(hasConfirmReceivedGoodsPermissionTransferId);

            // Staff don’t have permission Confirm received goods
            // click on [Received Goods] button in Transfer detail
            // Show restricted popup
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnReceiveGoods),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Product >> Transfer >> Confirm received goods.");
    }

    void checkCancelTransfer() {
        // check cancel actions at transfer detail page
        // get transfer info with seller token
        TransferInfo transferInfoWithSellerToken = new TransferManagement(sellerLoginInformation).getAllTransferInfo();
        int hasCancelPermissionTransferId = transferManagement.getCancelTransferPermissionTransferId(assignedBranchIds, transferInfoWithSellerToken);
        if (hasCancelPermissionTransferId != 0) {
            if (permissions.getProduct().getTransfer().isViewTransferDetail()) {
                // navigate to transfer detail page
                navigateToTransferDetailPage(hasCancelPermissionTransferId);

                // cancel actions
                commons.clickJS(loc_lnkSelectAction);
                if (permissions.getProduct().getTransfer().isCancelTransfer()) {
                    // Staff can cancel transfer if Have permission Cancel transfer
                    // and Assigned branches contains original/destination branch in transfer (exclude expired branch)
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddlListActions,
                                    commons.getListElement(loc_ddlListActions).size() - 1,
                                    loc_dlgConfirmation),
                            "Can not open confirmation cancel popup.");
                    if (!commons.getListElement(loc_dlgConfirmation).isEmpty())
                        commons.click(loc_dlgConfirmation_btnOK);
                } else {
                    // Staff don’t have permission Cancel transfer => Show restricted popup
                    // when: click [Cancel] button in Transfer detail
                    assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions,
                                    commons.getListElement(loc_ddlListActions).size() - 1),
                            "Restricted popup is not shown.");
                }
            }
        }

        // log
        logger.info("Check permission: Product >> Transfer >> Cancel transfer.");
    }
    public String getPageTitle(){
        return commons.getText(loc_lblPageTitle);
    }
    public TransferPage searchAndSelectProduct(List<APIProductDetailV2.ProductInfoV2> productInfoList){
        productInfoList.forEach(productInfo -> {
            String barcode = String.valueOf(productInfo.getId());
            if(productInfo.isHasModel()){
                barcode = barcode + " - "+productInfo.getVariationModelList().getFirst();
            }
            inputProductSearchTerm(productInfo.getName());
            selectProductByBarcode(barcode);
        });
        return this;
    }
    public TransferPage createTransfer(String originBranch, String destinationBranch, List<APIProductDetailV2.ProductInfoV2> productInfoList){
        new TransferPage(driver).navigateToCreateTransferPage();
        new TransferPage(driver).selectSourceBranch(originBranch)
                .selectDestinationBranch(destinationBranch)
                .searchAndSelectProduct(productInfoList)
                .clickSaveBtn();
        new HomePage(driver).waitTillSpinnerDisappear1().verifySuccessToastMessageShow();
        return this;
    }
    public String getOriginDestinationBranchName(int branchId){
        // init branch management API
        BranchManagement branchManagement = new BranchManagement(loginInformation);
        // get destination branches
        BranchInfo destinationInfo = branchManagement.getDestinationBranchesInfo();
        List<String> destinationBranchNames = destinationInfo.getBranchName();
        // index of origin branches
        int index = destinationInfo.getBranchID().indexOf(branchId);
        // get origin branch name
        String originBranchName = destinationBranchNames.get(index);
        // remove origin branch from destination branches
        destinationBranchNames.remove(originBranchName);
        String destinationBranch = destinationBranchNames.get(nextInt(destinationBranchNames.size()));
        return originBranchName +"-"+destinationBranch;
    }
    public TransferPage updateTransferredQuantity(){
        List<WebElement> inventoryList = commons.getElements(loc_lstSelectedProduct_lblInventory,2);
        inventoryList.forEach(inventory ->{
            int inventoryMax = Integer.parseInt(inventory.getText());
            int index = inventoryList.indexOf(inventory);
            commons.inputText(loc_lstSelectedProduct_lblInventory, index, String.valueOf(DataGenerator.generatNumberInBound(1, inventoryMax+1)));

        });
        return this;
    }
    public TransferPage navigateAndUpdateTransferredQuantity(int transferId){
        navigateToEditTransferPage(transferId);
        updateTransferredQuantity();
        clickSaveBtn();
        return this;
    }
    public int getSelectedProductNumber(){
        List<WebElement> quantityList = commons.getElements(loc_lstSelectProduct_txtQuantity,1);
        return quantityList.size();
    }
    public TransferPage navigateAndCancelTransfer(int transferId){
        navigateToTransferDetailPage(transferId);
        logger.info("Click on Select Action button.");
        commons.clickJS(loc_lnkSelectAction);
        logger.info("Click on Cancel button.");
        commons.clickJS(loc_ddlListActions, 1);
        new ConfirmationDialog(driver).clickOKBtn_V2();
        new HomePage(driver).verifySuccessToastMessageShow();
        return this;
    }
    public TransferPage navigateAndEditDestinationBranch(int transferId, String branchName){
        navigateToEditTransferPage(transferId);
        selectDestinationBranch(branchName);
        clickSaveBtn();
        new HomePage(driver).waitTillSpinnerDisappear1();
        new HomePage(driver).verifySuccessToastMessageShow();
        return this;
    }
}
