package pages.dashboard.promotion.flashsale.time;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static java.lang.Thread.sleep;

public class ManageTimePage extends ManageTimeElement {
    WebDriverWait wait;

    String pageLoadedTextENG = "Flash sale time management";
    String pageLoadedTextVIE = "Quản lý thời gian Flash sale";

    public ManageTimePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    Logger logger = LogManager.getLogger(ManageTimePage.class);

    public ManageTimePage verifyPageLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getPageSource().contains(pageLoadedTextVIE) || driver.getPageSource().contains(pageLoadedTextENG);
        });
        return this;
    }

    private void clickAddTimeBtn() throws InterruptedException {
        sleep(3000);
        wait.until(ExpectedConditions.visibilityOf(ADD_TIME_BTN)).click();
        logger.info("Click on the Add time button");
    }

    private void closeCreateSuccessfullyPopup() {
        wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BTN)).click();
        logger.info("Close Flash Sale Time created successfully! popup");
    }

    private void setTime(int startHour, int startMin) {
        // Set start time
        wait.until(ExpectedConditions.elementToBeClickable(START_HOUR)).click();
        waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(startHour))).click();
        logger.info("Set start hour: %s".formatted(startHour));

        wait.until(ExpectedConditions.elementToBeClickable(START_MIN)).click();
        waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(startMin))).click();
        logger.info("Set start min: %s".formatted(startMin));

        // Set end time
        wait.until(ExpectedConditions.elementToBeClickable(END_HOUR)).click();
        waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(startHour))).click();
        logger.info("Set end hour: %s".formatted(startHour));

        wait.until(ExpectedConditions.elementToBeClickable(END_MIN)).click();
        waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(startMin + 1))).click();
        logger.info("Set end min: %s".formatted(startMin + 1));

    }

    private void clickOnTheSaveBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        logger.info("Save flash sale time.");
    }

    public void addNewFlashSaleTime(int startHour, int startMin) throws InterruptedException {
        clickAddTimeBtn();
        setTime(startHour, startMin);
        clickOnTheSaveBtn();
        closeCreateSuccessfullyPopup();
    }

    /**
     * Wait until list element loading successfully
     */
    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }
}
