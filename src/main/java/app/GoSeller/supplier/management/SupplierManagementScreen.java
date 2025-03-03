package app.GoSeller.supplier.management;

import api.Seller.supplier.supplier.APISupplier;
import api.Seller.supplier.supplier.APISupplier.SupplierInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonMobile;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class SupplierManagementScreen extends SupplierManagementElement {
    WebDriver driver;
    UICommonMobile commonMobile;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;
    private final Logger logger = LogManager.getLogger();

    public SupplierManagementScreen(WebDriver driver) {
        this.driver = driver;
        commonMobile = new UICommonMobile(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public void openCreateSupplierScreen() {
        commonMobile.click(ADD_ICON);
    }

    public void openSupplierDetailScreen(String keywords) {
        commonMobile.getElement(SEARCH_BOX).clear();
        commonMobile.moveAndGetElementByText(keywords).click();
    }

    void checkSearchResult(String keywords, List<String> allSupName, List<String> allSupCode) {
        int numberOfMatches = 0;
        for (int index = 0; index < allSupCode.size(); index++) {
            if (allSupName.get(index).contains(keywords) || allSupCode.get(index).contains(keywords)) {
                numberOfMatches++;
            }
        }
        commonMobile.inputText(SEARCH_BOX, keywords);
        logger.info("[Supplier management] Check search result with keywords: %s.".formatted(keywords));

        boolean checkResult;
        if (numberOfMatches > 0) {
            List<String> supCodeList = commonMobile.getListElementText(SUPPLIER_CODE);
            List<String> supNameList = commonMobile.getListElementText(SUPPLIER_NAME);
            checkResult = (numberOfMatches == supNameList.size()) && IntStream.range(0, supCodeList.size()).allMatch(i -> supNameList.get(i).contains(keywords) || supCodeList.get(i).contains(keywords));
        } else
            checkResult = (commonMobile.getElements(SUPPLIER_NAME).isEmpty()) && (commonMobile.getElements(SUPPLIER_CODE).isEmpty());

        assertCustomize.assertTrue(checkResult, "[Failed][Supplier management screen] Result does not contains keywords, keywords: %s".formatted(keywords));
    }

    public void searchAndVerifySearchResult() {
        APISupplier.AllSupplierInformation allSupplierInformation = new APISupplier(loginInformation).getAllSupplierInformation();
        List<String> allSupCode = allSupplierInformation.getCodes();
        List<String> allSupName = allSupplierInformation.getNames();

        // search with a part of supplier name
        String keywords = allSupName.get(nextInt(allSupName.size()));
        keywords = keywords.substring(nextInt(keywords.length()));
        checkSearchResult(keywords, allSupName, allSupCode);

        // search with full supplier name
        keywords = allSupName.get(nextInt(allSupName.size()));
        checkSearchResult(keywords, allSupName, allSupCode);

        // search with a part of supplier code
        keywords = allSupCode.get(nextInt(allSupCode.size()));
        keywords = keywords.substring(nextInt(keywords.length()));
        checkSearchResult(keywords, allSupName, allSupCode);

        // search with full supplier code
        keywords = allSupCode.get(nextInt(allSupCode.size()));
        checkSearchResult(keywords, allSupName, allSupCode);

        // search with no result:
        keywords = String.valueOf(Instant.now().toEpochMilli());
        checkSearchResult(keywords, allSupName, allSupCode);
    }

    public void checkSupplierInformationAtSupplierManagementScreen(SupplierInformation supInfo) {
        commonMobile.inputText(SEARCH_BOX, supInfo.getCode());

        // check supplier name
        String supName = commonMobile.getText(SUPPLIER_NAME).replaceAll(".*?:\\s", "");
        assertCustomize.assertEquals(supName, supInfo.getName(), "[Failed][Supplier management screen] Supplier name should be %s, but found %s.".formatted(supName, supInfo.getName()));

        // check supplier code
        String supCode = commonMobile.getText(SUPPLIER_CODE).replaceAll(".*?:\\s", "");
        assertCustomize.assertEquals(supCode, supInfo.getCode(), "[Failed][Supplier management screen] Supplier code should be %s, but found %s.".formatted(supCode, supInfo.getCode()));

        // check supplier email
        String supEmail = commonMobile.getText(SUPPLIER_EMAIL).replaceAll(".*?:\\s", "");
        assertCustomize.assertEquals(supEmail, supInfo.getEmail(), "[Failed][Supplier management screen] Supplier email should be %s, but found %s.".formatted(supEmail, supInfo.getEmail()));

        // check supplier phone number
        String supPhone = commonMobile.getText(SUPPLIER_PHONE).replaceAll(".*?:\\s", "");
        assertCustomize.assertEquals(supPhone, supInfo.getPhoneNumber(), "[Failed][Supplier management screen] Supplier phone number should be %s, but found %s.".formatted(supPhone, supInfo.getPhoneNumber()));
    }

    public SupplierManagementScreen deleteSupplier(String supplierCode) {
        // find supplier
        WebElement supElement = commonMobile.moveAndGetElementByText(supplierCode);

        // swipe to show Delete button
        commonMobile.swipeHorizontalInPercent(supElement, 0.75, 0.5);

        // click Delete button
        commonMobile.getElement(DELETE_BTN, 10).click();

        // confirm delete supplier
        commonMobile.getElement(CONFIRM_POPUP_OK_BTN, 10).click();
        return this;
    }

    public void verifySupplierIsDeleted(String supplierCode) {
        commonMobile.inputText(SEARCH_BOX, supplierCode);

        assertCustomize.assertTrue(driver.findElements(SUPPLIER_CODE).isEmpty(), "[Failed][Supplier management screen] Supplier is deleted but it still shows on supplier management list, supplier code: %s.".formatted(supplierCode));
        logger.info("[Supplier management] Check deleted supplier.");
    }
}
