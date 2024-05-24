package web.Dashboard.orders.quotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utilities.links.Links.DOMAIN;

public class QuotationPage extends QuotationElement {

    final static Logger logger = LogManager.getLogger(QuotationPage.class);

    WebDriver driver;
    UICommonAction commonAction;
    HomePage homePage;
    AssertCustomize assertCustomize;

    public QuotationPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        homePage = new HomePage(driver);
    }

    public QuotationPage navigate() {
        homePage.navigateToPage("Orders", "Create Quotation");
        homePage.hideFacebookBubble();
        return this;
    }

    public WebElement getSubTotalElement() {
        return commonAction.getElement(loc_lblMoneyAmount, 0);
    }

    public WebElement getVATElement() {
        return commonAction.getElement(loc_lblMoneyAmount, 1);
    }

    public WebElement getTotalElement() {
        return commonAction.getElement(loc_lblMoneyAmount, 2);
    }

    public QuotationPage inputProductSearchTerm(String searchTerm) {
        commonAction.inputText(loc_txtSearchProduct, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search box.");
        return this;
    }

    /**
     *
     * @param searchBy Product/Barcode or Sản phẩm/Mã Vạch
     * @return
     */
    public QuotationPage selectSearchCondition(String searchBy) {
        commonAction.click(loc_ddlSearchBy);
        By conditionXpath = By.xpath("//div[@class='uik-select__label' and text()='%s']".formatted(searchBy));
        commonAction.click(conditionXpath);
        logger.info("Selected search condition: " + searchBy);
        return this;
    }

    /**
     * Temporary function, will be deleted soon
     * @return
     */
    public QuotationPage emptyProductSearchBox() {
        commonAction.sendKeys(loc_txtSearchProduct, "<>?@#$%^");

        for (int i = 0; i < 6; i++) {
            if (commonAction.getElements(loc_tmpProductSearchResult).isEmpty()) break;
            commonAction.sleepInMiliSecond(500);
        }

        logger.info("Emptied search box");
        return this;
    }

    /**
     * Retrieves the search results data
     * @return the list of lists containing search result information such as name, variation, barcode, price and conversion unit
     */
    public List<List<String>> getSearchResults() {
        //Wait till there are results on search results box
        for (int i = 0; i < 10; i++) {
            if (!commonAction.getElements(loc_tmpProductSearchResult).isEmpty()) break;
            commonAction.sleepInMiliSecond(500);
        }

        //Get data result data
        List<List<String>> table = new ArrayList<>();
        for (WebElement row : commonAction.getElements(loc_tmpProductSearchResult)) {
            List<String> rowData = new ArrayList<>();

            WebElement nameElement = row.findElement(By.xpath(PRODUCT_NAME_IN_RESULT.formatted("")));
            rowData.add(nameElement.getText()); //Get name

            List<WebElement> variations = row.findElements(By.xpath(VARIATION_IN_RESULT.formatted("")));
            String variationValue = variations.isEmpty() ? "" : variations.get(0).getText();
            rowData.add(variationValue); // Get variation

            WebElement barcodeElement = row.findElement(By.xpath(PRODUCT_BARCODE_IN_RESULT.formatted("")));
            rowData.add(barcodeElement.getText()); // Get Barcode

            WebElement priceElement = row.findElement(By.xpath(PRICE_IN_RESULT.formatted("")));
            rowData.add(priceElement.getText()); //Get price

            List<WebElement> conversionUnits = row.findElements(By.xpath(CONV_UNIT_IN_RESULT.formatted("")));
            String conversionValue = conversionUnits.isEmpty() ? "" : conversionUnits.get(0).getText();
            rowData.add(conversionValue); // Get conversion unit

            table.add(rowData);
        }
        return table;
    }

    /**
     * Selects a product from search results based on the provided data to add to quotation
     * @param productData The list of strings containing product name, variation, barcode, price and conversion unit
     * <p>Eg. String[] pr = {"Product with variations", "L|Red", "", "", ""};
     * <p>selectProduct(Arrays.asList(pr));
     * @return
     */
    public QuotationPage selectProduct(List<String> productData) {
        String name = productData.get(0);
        String variation = productData.get(1);
        String barcode = productData.get(2);
        String price = productData.get(3);
        String convUnit = productData.get(4);

        List<List<String>> results = getSearchResults();

        for (int i = 0; i < results.size(); i++) {
            if (!results.get(i).get(0).contentEquals(name)) continue;

            // If variation value is not provided, we proceed to next step
            if (!variation.isEmpty()) {
                if (!results.get(i).get(1).contentEquals(variation)) continue;
            }
            // If barcode value is not provided, we proceed to next step
            if (!barcode.isEmpty()) {
                if (!results.get(i).get(2).contentEquals(barcode)) continue;
            }
            // If price value is not provided, we proceed to next step
            if (!price.isEmpty()) {
                if (!results.get(i).get(3).contentEquals(price)) continue;
            }
            // If conversion unit value is not provided, we proceed to next step
            if (!convUnit.isEmpty()) {
                //Extract conversion unit value from string of "Unit: <conversion_unit>"
                String retrievedConvUnit = results.get(i).get(4);

                if (retrievedConvUnit.isEmpty()) continue;

                String extractedConvUnit = retrievedConvUnit.substring(retrievedConvUnit.indexOf(":") + 3);

                if (!extractedConvUnit.contentEquals(convUnit)) continue;
            }
            commonAction.click(loc_tmpProductSearchResult, i);

            /**
             * Code to check if the product is selected goes here
             */

            break;
        }
        logger.info("Selected product: " + name);
        return this;
    }

    public QuotationPage selectProduct(String name) {
        By productXpath = By.xpath(PRODUCT_NAME_IN_RESULT.formatted("and text()=\"%s\"".formatted(name)));
        commonAction.click(productXpath);
        logger.info("Selected product: " + name);
        return this;
    }

    public QuotationPage inputCustomerSearchTerm(String searchTerm) {
        homePage.hideFacebookBubble();
        commonAction.sendKeys(loc_txtSearchCustomer, searchTerm);
        logger.info("Input '" + searchTerm + "' into Customer Search box.");
        return this;
    }

    public QuotationPage selectCustomer(String name) {
        By customerXpath = By.xpath("//div[@class='full-name' and text()='%s']".formatted(name));
        commonAction.click(customerXpath);
        logger.info("Selected customer: " + name);
        return this;
    }

    /**
     * Retrieves the selected customer's name and phone number
     * @return A list of Strings containing the selected customer's name and phone number
     */
    public List<String> getSelectedCustomerData() {
        List<String> customerData = new ArrayList<>();

        String name = !commonAction.getElements(loc_lblSelectedCustomerName).isEmpty() ? "" : commonAction.getText(loc_lblSelectedCustomerName, 0);
        customerData.add(name); //Get name

        String phone = !commonAction.getElements(loc_lblSelectedCustomerPhone).isEmpty() ? "" : commonAction.getText(loc_lblSelectedCustomerPhone, 0);
        customerData.add(phone); // Get phone number

        return customerData;
    }

    public QuotationPage clickExportQuotationBtn() {
        homePage.hideFacebookBubble();
        commonAction.click(loc_btnCreate);
        logger.info("Clicked on 'Export Quotation' button.");
        commonAction.sleepInMiliSecond(5000);
        return this;
    }

    public QuotationPage inputQuantity(String number) {
        commonAction.sendKeys(loc_txtQuantity, number);
        logger.info("Inputted " + number + " into quantity box.");
        return this;
    }

    public QuotationPage removeItemFromListQuotation() {
        homePage.hideFacebookBubble();
        commonAction.click(loc_btnRemoveProduct);
        logger.info("Clicked on Delete icon on list product quotation.");
        return this;
    }

    public QuotationPage confirmProductRemoval() {
        new ConfirmationDialog(driver).clickOKBtn();
        return this;
    }

    public String getSubTotal() {
        logger.info("Getting Subtotal...");
        return commonAction.getText(getSubTotalElement().findElement(By.tagName("b")));
    }

    public String getVAT() {
        logger.info("Getting VAT...");
        return commonAction.getText(getVATElement().findElement(By.tagName("span")));
    }

    public String getTotal() {
        logger.info("Getting Total...");
        return commonAction.getText(getTotalElement().findElement(By.tagName("span")));
    }

    public List<List<String>> readQuotationFile() {

        int STORE_NAME_ROW = 0;
        int STORE_NAME_COL = 1;
        int STORE_PHONE_ROW = 1;
        int STORE_PHONE_COL = 1;
        int STORE_EMAIL_ROW = 2;
        int STORE_EMAIL_COL = 1;
        int CUSTOMER_INFO_ROW = 4;
        int CUSTOMER_NAME_ROW = 5;
        int CUSTOMER_NAME_COL = 1;
        int CUSTOMER_PHONE_ROW = 6;
        int CUSTOMER_PHONE_COL = 1;
        int CUSTOMER_EMAIL_ROW = 7;
        int CUSTOMER_EMAIL_COL = 1;
        int PRODUCT_START_ROW = 9;
        int HEADER_ROW = 8;
        int NUMBER_COL = 0;
        int IMAGE_COL = 1;
        int PRODUCT_NAME_COL = 2;
        int QUANTITY_COL = 3;
        int UNIT_PRICE_COL = 4;
        int TOTAL_PRICE_COL = 5;

        Excel excel = new Excel();

        Sheet dataSheet = null;
        try {
            dataSheet = excel.getSheet(FileUtils.getLastDownloadedFile(FileNameAndPath.downloadFolder), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<List<String>> table = new ArrayList<>();

        // Store Information
        String storeNameTitle = dataSheet.getRow(STORE_NAME_ROW).getCell(0).getStringCellValue();
        String storeNameValue = dataSheet.getRow(STORE_NAME_ROW).getCell(STORE_NAME_COL).getStringCellValue();
        table.add(Arrays.asList(storeNameTitle, storeNameValue));

        String storePhoneTitle = dataSheet.getRow(STORE_PHONE_ROW).getCell(0).getStringCellValue();
        String storePhoneValue = dataSheet.getRow(STORE_PHONE_ROW).getCell(STORE_PHONE_COL).getStringCellValue();
        table.add(Arrays.asList(storePhoneTitle, storePhoneValue));

        String storeEmailTitle = dataSheet.getRow(STORE_EMAIL_ROW).getCell(0).getStringCellValue();
        String storeEmailValue = dataSheet.getRow(STORE_EMAIL_ROW).getCell(STORE_EMAIL_COL).getStringCellValue();
        table.add(Arrays.asList(storeEmailTitle, storeEmailValue));

        // Customer Information
        String customerInfoTitle = dataSheet.getRow(CUSTOMER_INFO_ROW).getCell(0).getStringCellValue();
        table.add(Arrays.asList(customerInfoTitle));

        String customerNameTitle = dataSheet.getRow(CUSTOMER_NAME_ROW).getCell(0).getStringCellValue();
        String customerNameValue = "";
        if (!excel.isCellBlank(dataSheet.getRow(5).getCell(CUSTOMER_NAME_COL))) {
            customerNameValue = dataSheet.getRow(CUSTOMER_NAME_ROW).getCell(CUSTOMER_NAME_COL).getStringCellValue();
        }
        table.add(Arrays.asList(customerNameTitle, customerNameValue));

        String customerPhoneTitle = dataSheet.getRow(CUSTOMER_PHONE_ROW).getCell(0).getStringCellValue();
        String customerPhoneValue = "";
        if (!excel.isCellBlank(dataSheet.getRow(CUSTOMER_PHONE_ROW).getCell(CUSTOMER_PHONE_COL))) {
            customerPhoneValue = dataSheet.getRow(CUSTOMER_PHONE_ROW).getCell(CUSTOMER_PHONE_COL).getStringCellValue();
        }
        table.add(Arrays.asList(customerPhoneTitle, customerPhoneValue));

        String customerEmailTitle = dataSheet.getRow(CUSTOMER_EMAIL_ROW).getCell(0).getStringCellValue();
        String customerEmailValue = "";
        if (!excel.isCellBlank(dataSheet.getRow(CUSTOMER_EMAIL_ROW).getCell(CUSTOMER_EMAIL_COL))) {
            customerEmailValue = dataSheet.getRow(CUSTOMER_EMAIL_ROW).getCell(CUSTOMER_EMAIL_COL).getStringCellValue();
        }
        table.add(Arrays.asList(customerEmailTitle, customerEmailValue));

        // Get Header
        String numberTitle = dataSheet.getRow(HEADER_ROW).getCell(NUMBER_COL).getStringCellValue();
        String imageTitle = dataSheet.getRow(HEADER_ROW).getCell(IMAGE_COL).getStringCellValue();
        String productNameTitle = dataSheet.getRow(HEADER_ROW).getCell(PRODUCT_NAME_COL).getStringCellValue();
        String quantityTitle = dataSheet.getRow(HEADER_ROW).getCell(QUANTITY_COL).getStringCellValue();
        String unitPriceTitle = dataSheet.getRow(HEADER_ROW).getCell(UNIT_PRICE_COL).getStringCellValue();
        String totalPriceTitle = dataSheet.getRow(HEADER_ROW).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(numberTitle, imageTitle, productNameTitle, quantityTitle, unitPriceTitle, totalPriceTitle));

        //Get Quotation Data
        int lastRowIndex = dataSheet.getLastRowNum();
        int lastProductRowIndex = lastRowIndex - 3;

        for (int i = PRODUCT_START_ROW; i <= lastProductRowIndex; i++) {
            List<String> rowData = new ArrayList<>();
            for (int j = NUMBER_COL; j <= TOTAL_PRICE_COL; j++) {
                rowData.add(dataSheet.getRow(i).getCell(j).getStringCellValue());
            }
            table.add(rowData);
        }

        String subTotalTitle = dataSheet.getRow(lastRowIndex - 2).getCell(UNIT_PRICE_COL).getStringCellValue();
        String subTotalValue = dataSheet.getRow(lastRowIndex - 2).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(subTotalTitle, subTotalValue));

        String vatTitle = dataSheet.getRow(lastRowIndex - 1).getCell(UNIT_PRICE_COL).getStringCellValue();
        String vatValue = dataSheet.getRow(lastRowIndex - 1).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(vatTitle, vatValue));

        String totalTitle = dataSheet.getRow(lastRowIndex).getCell(UNIT_PRICE_COL).getStringCellValue();
        String totalValue = dataSheet.getRow(lastRowIndex).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(totalTitle, totalValue));

        return table;
    }

    public void verifyTextAtCreateQuotationScreen() throws Exception {

        String text = commonAction.getText(loc_lblPageTitle).split("\n")[0];
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.title"), text);

        text = commonAction.getText(loc_ddlSearchBy);
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchProductByName"), text);

        text = commonAction.getAttribute(loc_txtSearchProduct, "placeholder");
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchProductByName.placeHolder"));

        text = commonAction.getText(loc_lblTableHeader);
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.table.header"), text);

        text = commonAction.getAttribute(loc_txtSearchCustomer, "placeholder");
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchCustomer.placeHolder"));

        text = commonAction.getText(getSubTotalElement().findElement(By.tagName("p")));
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.subTotal"), text);

        text = commonAction.getText(getVATElement().findElement(By.tagName("p")));
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.vat"), text);

        text = commonAction.getText(getTotalElement().findElement(By.tagName("b")));
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.total"), text);

        text = commonAction.getText(loc_btnCreate);
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.exportBtn"), text);

        logger.info("verifyTextAtCreateQuotationScreen completed");
    }

    public void verifyErrorWhenSelectingProductWithConversionUnits() throws Exception {
        String text = homePage.getToastMessage();
        Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.error.addConversionUnits"), text);
        logger.info("verifyErrorWhenSelectingProductWithConversionUnits completed");
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateQuotation(String permission, String url) {
        if (permission.contentEquals("A")) {
            inputProductSearchTerm("Test Permission");
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
        }
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/browse/BH-24813
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginDashboardInfo staffLoginInfo;

    public QuotationPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public void checkQuotationPermission() {
        driver.get("%s/order/instore-quotation".formatted(DOMAIN));
        if (permissions.getOrders().getQuotation().isCreateQuotation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/order/instore-quotation".formatted(DOMAIN), "instore-quotation"),
                    "Quotation page must be shown, but found %s.".formatted(driver.getCurrentUrl()));
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/order/instore-quotation".formatted(DOMAIN)), "Restricted page is not shown.");
        }
        logger.info("Check permission: Orders >> Quotation >> Check create quotation.");
    }
}
