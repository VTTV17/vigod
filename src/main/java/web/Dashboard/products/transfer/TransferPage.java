package web.Dashboard.products.transfer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

import java.util.ArrayList;
import java.util.List;

public class TransferPage extends TransferElement {
    WebDriver driver;
    UICommonAction commons;

    final static Logger logger = LogManager.getLogger(TransferPage.class);

    public TransferPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
    }
    HomePage homePage;
    public TransferPage navigate() {
        homePage = new HomePage(driver);
        homePage.navigateToPage("Products", "Transfer");
        commons.waitVisibilityOfElementLocated(loc_btnCreateTransfer);
        return this;
    }

    public void waitTillPageStable() {
        commons.waitVisibilityOfElementLocated(loc_btnCreateTransfer);
        homePage.waitTillLoadingDotsDisappear().waitTillSpinnerDisappear1();
    }

    public TransferPage clickAddTransferBtn() {
        commons.click(loc_btnCreateTransfer);
        commons.waitVisibilityOfElementLocated(loc_txtNote);
        logger.info("Clicked on 'Add Transfer' button.");
        commons.sleepInMiliSecond(1000);
        return this;
    }

    public TransferPage inputSearchTerm(String searchTerm) {
        commons.sendKeys(loc_txtSearchRecord, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search box.");
        homePage.waitTillSpinnerDisappear();
        return this;
    }

    public TransferPage inputProductSearchTerm(String searchTerm) {
        commons.sendKeys(loc_txtSearchProduct, searchTerm);
        logger.info("Input '" + searchTerm + "' into Product Search box.");
        commons.sleepInMiliSecond(500); //There's a delay of 500ms before search operation commences
        By searchLoadingIcon = By.xpath("//div[contains(@class,'search-result')]/div[contains(@class,'loading')]");
        commons.waitInvisibilityOfElementLocated(searchLoadingIcon);
        return this;
    }

    public TransferPage selectProduct(String name) {
        By productXpath = By.xpath(PRODUCT_NAME_IN_RESULT.formatted("and text()=\"%s\"".formatted(name)));
        commons.click(productXpath);
        logger.info("Selected product: " + name);
        return this;
    }

    public TransferPage selectSourceBranch(String name) {
        commons.click(loc_ddlBranches, 0);
        By branchName = By.xpath("//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']".formatted(name));
        commons.click(branchName);
        logger.info("Selected source branch: " + name);
        return this;
    }

    public void getSearchResults() {
        By results = By.xpath("//div[contains(@class,'search-result')]/div[contains(@class,'product-item')]");

        By name = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=1 %s]".formatted(""));
        By barcode = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=2 %s]".formatted(""));
        By variation = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=3 %s]".formatted(""));
        By inventory = By.xpath(".//div[contains(@class,'search-item')]/span/p[position()=1 %s]".formatted(""));
        By unit = By.xpath(".//div[contains(@class,'search-item')]/span/p[position()=2 %s]".formatted(""));

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
        By branchName = By.xpath("//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']".formatted(name));
        commons.click(branchName);
        logger.info("Selected destination branch: " + name);
        return this;
    }

    public TransferPage inputTransferredQuantity(int quantity) {
        commons.sendKeys(loc_btnTransferredQuantity, String.valueOf(quantity));
        logger.info("Input '" + quantity + "' into Transferred Quantity field.");
        return this;
    }

    public void selectIMEI(String imei) {
        By imeiLocator = By.xpath("(//div[@class='code in-purchase'])[2]/div[@class='content']/p[text()='%s']".formatted(imei));
        commons.click(imeiLocator);
        logger.info("Selected IMEI: " + imei);
    }

    public TransferPage selectIMEI(String[] imei) {
        commons.click(new ByChained(loc_btnTransferredQuantity, By.xpath(".//ancestor::div[@class='number']//following-sibling::span")));
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
        By record = By.cssSelector("[href='/product/transfer/wizard/%s']".formatted(recordID));
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


    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-24651
    void checkViewTransferList() {

    }
}
