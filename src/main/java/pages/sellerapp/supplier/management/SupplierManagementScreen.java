package pages.sellerapp.supplier.management;

import api.dashboard.products.SupplierAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import pages.sellerapp.login.LoginPage;
import utilities.UICommonMobile;
import utilities.assert_customize.AssertCustomize;
import utilities.model.sellerApp.supplier.SupplierInformation;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class SupplierManagementScreen extends SupplierManagementElement {
    WebDriver driver;
    UICommonMobile commonMobile;
    AssertCustomize assertCustomize;
    private static int countFail = 0;
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
                numberOfMatches ++;
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
            checkResult = (commonMobile.getElements(SUPPLIER_NAME).size() == 0) && (commonMobile.getElements(SUPPLIER_CODE).size() == 0);

        countFail = assertCustomize.assertTrue(countFail, checkResult, "[Failed][Supplier management screen] Result does not contains keywords, keywords: %s".formatted(keywords));
    }

    public void searchAndVerifySearchResult() {
        List<SupplierInformation> allSupplierInformation = new SupplierAPI(new LoginPage(driver).getLoginInfo()).getListSupplierInformation();
        List<String> allSupCode = new ArrayList<>();
        List<String> allSupName = new ArrayList<>();
        allSupplierInformation.forEach(supplierInformation -> {
            allSupCode.add(supplierInformation.getSupplierCode());
            allSupName.add(supplierInformation.getSupplierName());
        });

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

    public void checkSupplierInformationAtSupplierManagementScreen(SupplierInformation supInfo) throws IOException {
        commonMobile.inputText(SEARCH_BOX, supInfo.getSupplierCode());

        // check supplier name
        String supName = commonMobile.getText(SUPPLIER_NAME).replaceAll(".*?:\\s", "");
        countFail = assertCustomize.assertEquals(countFail, supName, supInfo.getSupplierName(),"[Failed][Supplier management screen] Supplier name should be %s, but found %s.".formatted(supName, supInfo.getSupplierName()));

        // check supplier code
        String supCode = commonMobile.getText(SUPPLIER_CODE).replaceAll(".*?:\\s", "");
        countFail = assertCustomize.assertEquals(countFail, supCode, supInfo.getSupplierCode(),"[Failed][Supplier management screen] Supplier code should be %s, but found %s.".formatted(supCode, supInfo.getSupplierCode()));

        // check supplier email
        String supEmail = commonMobile.getText(SUPPLIER_EMAIL).replaceAll(".*?:\\s", "");
        countFail = assertCustomize.assertEquals(countFail, supEmail, supInfo.getSupplierEmail(),"[Failed][Supplier management screen] Supplier email should be %s, but found %s.".formatted(supEmail, supInfo.getSupplierEmail()));

        // check supplier phone number
        String supPhone = commonMobile.getText(SUPPLIER_PHONE).replaceAll(".*?:\\s", "");
        countFail = assertCustomize.assertEquals(countFail, supPhone, supInfo.getSupplierPhone(),"[Failed][Supplier management screen] Supplier phone number should be %s, but found %s.".formatted(supPhone, supInfo.getSupplierPhone()));
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

        countFail = assertCustomize.assertTrue(countFail, driver.findElements(SUPPLIER_CODE).size() == 0,"[Failed][Supplier management screen] Supplier is deleted but it still shows on supplier management list, supplier code: %s.".formatted(supplierCode));
        logger.info("[Supplier management] Check deleted supplier.");
    }
}
