package web.Dashboard.products.productcollection.createeditproductcollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utilities.links.Links.DOMAIN;

public class EditProductCollection extends CreateProductCollection {
    WebDriver driver;
    ProductCollectionManagement productCollectionManagement;
    final static Logger logger = LogManager.getLogger(EditProductCollection.class);

    public EditProductCollection(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        generator = new DataGenerator();
        home = new HomePage(driver);
    }

    HomePage home;

    public EditProductCollection navigateEditCollection(String collectioName, String languageDashboard) throws Exception {

        home.waitTillSpinnerDisappear();
        home.navigateToPage("Products", "Product Collections");
        home.selectLanguage(languageDashboard);
        home.hideFacebookBubble();
        productCollectionManagement = new ProductCollectionManagement(driver);
        return productCollectionManagement.goToEditProductCollection(collectioName);
    }

    public ProductCollectionManagement editProductPriorityInCollection() {
        home.waitTillSpinnerDisappear1();
        CreateProductCollection.productPriorityMap = inputPriority(false, true);
        clickOnSaveBTN();//Click outside
        clickOnSaveBTN();
        logger.info("Edit priority of product.");
        return clickOnClose();
    }

    public ProductCollectionManagement editProductListInManualCollection(String[] newProductList, boolean hasDeleteProduct, boolean hasInputPriority) {
        home.waitTillSpinnerDisappear1();
        int productSize = common.getElements(loc_icn_btnDelete).size();
        System.out.println("productSize: " + productSize);
        if (hasDeleteProduct) {
            for (int i = 0; i < productSize; i++) {
                common.click(loc_icn_btnDelete, 0);
                System.out.println("Deleted: " + i);
                common.sleepInMiliSecond(500);
            }
        }
        selectProductWithKeyword(newProductList);
        if (hasInputPriority) {
            CreateProductCollection.productPriorityMap = inputPriority(false, true);
            clickOnSaveBTN();//Click outside
        } else {
            CreateProductCollection.productPriorityMap = getProductPriorityMapBefore();
        }
        clickOnSaveBTN();
        logger.info("Edit product list in manual collection.");
        return clickOnClose();
    }

    public String[] getCollectionConditionBefore() {
        List<String> conditionList = new ArrayList<>();
        int conditionSize = common.getElements(loc_lst_txtConditionValue).size();
        for (int i = 0; i < conditionSize; i++) {
            String condition = common.getDropDownSelectedValue(loc_lst_ddlCondition, i);
            String operate = common.getDropDownSelectedValue(loc_lst_ddlOperator, i);
            String value = common.getAttribute(loc_lst_txtConditionValue, i, "value");
            String aCondition = condition + "-" + operate + "-" + value;
            conditionList.add(aCondition);
        }
        logger.info("Conditions selected before: " + conditionList);
        String[] conditions = new String[conditionList.size()];
        return conditionList.toArray(conditions);
    }

    public String[] EditAutomationCollection(String conditionType, String... conditions) throws Exception {
        home.waitTillSpinnerDisappear();
        common.sleepInMiliSecond(1000);
        selectConditionType(conditionType);
        String[] conditionsAvailable = getCollectionConditionBefore();
        List<String> allList = new ArrayList<>();
        allList.addAll(Arrays.stream(conditionsAvailable).toList());
        allList.addAll(Arrays.stream(conditions).toList());
        System.out.println("allList: " + allList);
        String[] allConditionArr = new String[allList.size()];
        allList.toArray(allConditionArr);
        selectCondition(true, allList.toArray(allConditionArr));
        clickOnSaveBTN();
        clickOnClose();
        logger.info("Update Automated collection successfully.");
        return allConditionArr;
    }

    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-24652
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;

    void navigateToProductCollectionDetailPage(int productCollectionId) {
        // navigate to product collection detail page by URL
        if (!driver.getCurrentUrl().contains("/collection/edit/product/%s".formatted(productCollectionId))) {
            driver.get("%s/collection/edit/product/%s".formatted(DOMAIN, productCollectionId));
            logger.info("Navigate to product collection detail page by URL, collectionId: %s.".formatted(productCollectionId));
        }
    }

    List<Integer> collectionIds;

    public void checkViewCollectionsDetail(AllPermissions permissions, List<Integer> collectionIds) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init assert customize
        assertCustomize = new AssertCustomize(driver);

        // get list collection index
        this.collectionIds = collectionIds;
        if (!collectionIds.isEmpty()) {
            // check permission
            if (permissions.getProduct().getCollection().isViewCollectionDetail()) {
                // check can view product collection detail
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/collection/edit/product/%s".formatted(DOMAIN, collectionIds.get(0)),
                                String.valueOf(collectionIds.get(0))),
                        "Product collection detail page must be shown instead of %s.".formatted(driver.getCurrentUrl()));

                // check edit translation
                checkEditTranslation();

                // check delete translation
                checkDeleteCollection();

                // check edit collection
                checkEditCollection();
            } else {
                // Show restricted page
                // if staff don’t have permission “View collection detail” when open collection detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/collection/edit/product/%s".formatted(DOMAIN, collectionIds.get(0))),
                        "Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
            }
            logger.info("Check permission: Product >> Collection >> View collection detail.");
        } else logger.info("Store does not have any product collection.");
    }

    void checkEditCollection() {
        if (!collectionIds.isEmpty()) {
            // navigate to product collection detail page
            navigateToProductCollectionDetailPage(collectionIds.get(0));

            // check permission
            if (permissions.getProduct().getCollection().isEditCollection()) {
                // check can edit product collection
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave, loc_dlgNotification),
                        "Notification popup is not shown.");
                if (!common.getListElement(loc_dlgNotification).isEmpty())
                    common.closePopup(loc_dlgNotification_btnClose);
            } else {
                // Show restricted popup
                // when click on [Save] button in collection detail
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave),
                        "Restricted popup is not shown.");
            }
        } else logger.info("Store does not have any product collection.");
        logger.info("Check permission: Product >> Collection >> Edit collection.");
    }

    void checkDeleteCollection() {
        if (!collectionIds.isEmpty()) {
            // navigate to product collection detail page
            navigateToProductCollectionDetailPage(collectionIds.get(0));

            // open confirm delete popup
            common.openPopupJS(loc_btnDelete, loc_dlgConfirmation);

            // check permission
            if (permissions.getProduct().getCollection().isDeleteCollection()) {
                // confirm delete
                common.closePopup(loc_dlgConfirmation_btnOK);

                // remove deleted collections
                collectionIds.remove(0);
            } else {
                // Show restricted popup
                // when click on [OK] button in confirm delete collection popup
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgConfirmation_btnOK),
                        "Restricted popup is not shown.");
            }
        } else logger.info("Store does not have any product collection.");
        logger.info("Check permission: Product >> Collection >> Delete collection.");
    }

    void checkEditTranslation() {
        if (!collectionIds.isEmpty()) {
            // navigate to product collection detail page
            navigateToProductCollectionDetailPage(collectionIds.get(0));

            // check permission
            if (permissions.getProduct().getCollection().isEditTranslation()) {
                // check can edit product collection translation
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnEditTranslation, loc_dlgEditTranslation),
                        "Edit translation popup is not shown.");

                // check can edit product collection translation
                if (!common.getListElement(loc_dlgEditTranslation).isEmpty())
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgEditTranslation_btnSave, loc_dlgToastSuccess),
                            "Can not update product collection translation.");
            } else {
                // Show restricted popup
                // when click on [Edit translate] button in collection detail
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnEditTranslation),
                        "Restricted popup is not shown.");
            }
        } else logger.info("Store does not have any product collection.");
        logger.info("Check permission: Product >> Collection >> Edit translate.");
    }
}
