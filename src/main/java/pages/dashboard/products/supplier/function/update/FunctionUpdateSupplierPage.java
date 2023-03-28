package pages.dashboard.products.supplier.function.update;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.products.supplier.function.management.FunctionSupplierManagementPage;
import pages.dashboard.products.supplier.ui.update.UIUpdateSupplierPage;
import utilities.UICommonAction;

import java.time.Duration;
import java.time.Instant;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.openqa.selenium.Keys.CONTROL;
import static org.openqa.selenium.Keys.DELETE;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class FunctionUpdateSupplierPage extends FunctionUpdateSupplierElement {

    Logger logger = LogManager.getLogger(FunctionUpdateSupplierPage.class);

    WebDriverWait wait;
    Actions act;
    String language;
    UICommonAction commonAction;
    UIUpdateSupplierPage uiUpdateSupplierPage;

    public FunctionUpdateSupplierPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        act = new Actions(driver);
        commonAction = new UICommonAction(driver);
        uiUpdateSupplierPage = new UIUpdateSupplierPage(driver);
    }

    public FunctionUpdateSupplierPage setLanguage(String language) {
        this.language = language;
        // set dashboard language
        String currentLanguage= HEADER_SELECTED_LANGUAGE.getText();;

        commonAction.sleepInMiliSecond(1000);

        if (!currentLanguage.contains(language)) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", HEADER_SELECTED_LANGUAGE);
            HEADER_LANGUAGE_LIST.stream().filter(webElement -> webElement.getText().contains(language))
                    .findFirst().ifPresent(webElement -> ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click()", webElement));
        }

        return this;
    }

    void inputSupplierName(String name) {
        // input supplier name
        wait.until(elementToBeClickable(SUPPLIER_NAME)).click();
        SUPPLIER_NAME.sendKeys(CONTROL + "a", DELETE);
        SUPPLIER_NAME.sendKeys(name);
        logger.info("Input supplier name: %s".formatted(name));
    }

    void inputSupplierCode(String code) {
        // input supplier code
        wait.until(elementToBeClickable(SUPPLIER_CODE)).click();
        SUPPLIER_CODE.sendKeys(CONTROL + "a", DELETE);
        SUPPLIER_CODE.sendKeys(code);
        logger.info("Input supplier code: %s".formatted(code));

    }

    void inputPhoneNumber(String phone) {
        // input phone number
        wait.until(elementToBeClickable(PHONE_NUMBER)).click();
        PHONE_NUMBER.sendKeys(CONTROL + "a", DELETE);
        PHONE_NUMBER.sendKeys(phone);
        logger.info("Input phone number: %s".formatted(phone));
    }

    void inputEmail(String email) {
        // input email
        wait.until(elementToBeClickable(EMAIL)).click();
        EMAIL.sendKeys(CONTROL + "a", DELETE);
        EMAIL.sendKeys(email);
        logger.info("Input email: %s".formatted(email));
    }

    void selectCountry(boolean isVNSupplier) {
        // open country dropdown
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", COUNTRY_DROPDOWN);
        commonAction.sleepInMiliSecond(1000);

        // check supplier has locator on VN or not
        if (isVNSupplier) {
            // select country = Vietnam
            new Select(COUNTRY_DROPDOWN).selectByVisibleText("Vietnam");
            logger.info("Select country: Vietnam");
        } else {
            // select another country
            int index = nextInt(COUNTRY_LIST.size());
            while (COUNTRY_LIST.get(index).getText().equals("Vietnam")) index = nextInt(COUNTRY_LIST.size());
            new Select(COUNTRY_DROPDOWN).selectByIndex(index);
            logger.info("Select country: %s".formatted(COUNTRY_LIST.get(index).getText()));
        }

        commonAction.sleepInMiliSecond(1000);
    }

    void inputAddress(String address) {
        // input address
        act.moveToElement(ADDRESS).click().build().perform();
        ADDRESS.sendKeys(CONTROL + "a", DELETE);
        ADDRESS.sendKeys(address);
        logger.info("Input address: %s".formatted(address));
    }

    /* VN address */
    void selectVNCity() {
        // open city dropdown
        wait.until(elementToBeClickable(VN_CITY_DROPDOWN)).click();

        commonAction.sleepInMiliSecond(1000);

        // get index of selected city
        int index = nextInt(VN_CITY_LIST.size());
        new Select(VN_CITY_DROPDOWN).selectByIndex(index);
        logger.info("Select city: %s".formatted(VN_CITY_LIST.get(index).getText()));
    }

    void selectVNDistrict() {
        // open district dropdown
        wait.until(elementToBeClickable(VN_DISTRICT_DROPDOWN)).click();

        commonAction.sleepInMiliSecond(1000);

        // get index of selected district
        int index = nextInt(VN_DISTRICT_LIST.size());
        new Select(VN_DISTRICT_DROPDOWN).selectByIndex(index);
        logger.info("Select district: %s".formatted(VN_DISTRICT_LIST.get(index).getText()));
    }

    void selectVNWard() {
        // open ward dropdown
        wait.until(elementToBeClickable(VN_WARD_DROPDOWN)).click();

        commonAction.sleepInMiliSecond(1000);

        // get index of selected ward
        int index = nextInt(VN_WARD_LIST.size());
        new Select(VN_WARD_DROPDOWN).selectByIndex(index);
        logger.info("Select ward: %s".formatted(VN_WARD_LIST.get(index).getText()));
    }

    /* non-VN address */
    void inputNonVNAddress2(String address2) {
        // input address 2 for non-VN country
        act.moveToElement(NON_VN_ADDRESS2).click().build().perform();
        NON_VN_ADDRESS2.sendKeys(CONTROL + "a", DELETE);
        NON_VN_ADDRESS2.sendKeys(address2);
        logger.info("Input address2: %s".formatted(address2));
    }

    void inputNonVNCity(String city) {
        // input city for non-VN country
        act.moveToElement(NON_VN_CITY).click().build().perform();
        NON_VN_CITY.sendKeys(CONTROL + "a", DELETE);
        NON_VN_CITY.sendKeys(city);
        logger.info("Input city: %s".formatted(city));
    }

    void selectNonVNProvince() {
        // open province dropdown
        wait.until(elementToBeClickable(NON_VN_PROVINCE_DROPDOWN)).click();

        commonAction.sleepInMiliSecond(1000);

        // get index of selected province
        int index = nextInt(NON_VN_PROVINCE_LIST.size());
        new Select(NON_VN_PROVINCE_DROPDOWN).selectByIndex(index);
        logger.info("Select province: %s".formatted(NON_VN_PROVINCE_LIST.get(index).getText()));
    }

    void inputNonVNZipcode(String zipcode) {
        // input zipcode for non-VN country
        act.moveToElement(NON_VN_ZIP_CODE).click().build().perform();
        NON_VN_ZIP_CODE.sendKeys(CONTROL + "a", DELETE);
        NON_VN_ZIP_CODE.sendKeys(zipcode);
        logger.info("Input zipcode: %s".formatted(zipcode));
    }

    void selectResponsibleStaff() {
        // open responsible staff dropdown
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", RESPONSIBLE_STAFF_DROPDOWN);
        commonAction.sleepInMiliSecond(1000);

        // get index of selected staff
        int index = nextInt(RESPONSIBLE_STAFF_LIST.size());
        new Select(RESPONSIBLE_STAFF_DROPDOWN).selectByIndex(index);
        logger.info("Select responsible staff: %s".formatted(RESPONSIBLE_STAFF_LIST.get(index).getText()));
    }

    void inputDescription(String description) {
        act.moveToElement(DESCRIPTION).click().build().perform();
        DESCRIPTION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        DESCRIPTION.sendKeys(description);
        logger.info("Input description: %s".formatted(description));
    }

    void completeCreateSupplier() {
        act.moveToElement(HEADER_SAVE_BTN).click().build().perform();
        logger.info("Complete create supplier");
    }

    public void updateNewSupplier(boolean isVNSupplier) throws Exception {
        // navigate to create supplier page
        new FunctionSupplierManagementPage(driver).navigateToSupplierManagementPage();

        // select country
        selectCountry(isVNSupplier);

        // check UI
//        uiUpdateSupplierPage.checkUIAddSupplierPage(language, isVNSupplier);

        // generate data
        String epoch = String.valueOf(Instant.now().toEpochMilli());

        // input supplier name
        inputSupplierName("[%s] Auto - Supplier %s - %s".formatted(language, isVNSupplier ? "VN" : "Non VN", epoch));

        // input supplier code
        inputSupplierCode("%s".formatted(epoch));

        // input supplier phone number
        inputPhoneNumber(epoch);

        // input supplier email
        inputEmail("%s@qa.team".formatted(epoch));

        // input address
        inputAddress("Address %s".formatted(epoch));

        // if country = Vietnam
        if (isVNSupplier) {
            // select city
            selectVNCity();

            // select district
            selectVNDistrict();

            // select ward
            selectVNWard();
        }
        // else country = non-Vietnam
        else {
            // input address2
            inputNonVNAddress2("Address2 %s".formatted(epoch));

            // input city
            inputNonVNCity("City %s".formatted(epoch));

            // select province
            selectNonVNProvince();

            // input zipcode
            inputNonVNZipcode("Zipcode %s".formatted(epoch));
        }

        // select responsible staff
        selectResponsibleStaff();

        // input description
        inputDescription("Descriptions %s".formatted(epoch));

        // click Save button to complete create supplier
        completeCreateSupplier();

        if (uiUpdateSupplierPage.countFail > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(uiUpdateSupplierPage.countFail));
        }
    }

}
