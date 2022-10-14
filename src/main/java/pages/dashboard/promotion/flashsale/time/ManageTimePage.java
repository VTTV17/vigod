package pages.dashboard.promotion.flashsale.time;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static java.lang.Thread.sleep;

public class ManageTimePage extends ManageTimeElement {
    WebDriverWait wait;
    UICommonAction commonAction;

    String pageLoadedTextENG = "Flash sale time management";
    String pageLoadedTextVIE = "Quản lý thigh gian Flash sale";

    public static int startHour;
    public static int startMin;
    public static int endHour;
    public static int endMin;
    public static int incDay;

    public ManageTimePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
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
        wait.until(ExpectedConditions.urlContains("/flash-sale/time/list"));
        wait.until(ExpectedConditions.visibilityOf(ADD_TIME_BTN)).click();
        logger.info("Click on the Add time button");
    }

    private void closeCreateSuccessfullyPopup() {
        wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BTN)).click();
        logger.info("Close Flash Sale Time created successfully! popup");
    }

    private List<Integer> getStartTime(int hour, int min) {
        // get current hour
        int currentHour = LocalTime.now().getHour();

        // backup currentHour
        int currentHourCheck = currentHour;

        // get current min
        int currentMin = LocalTime.now().getMinute();

        // check current min, maximum star time is 23:58 => start min always < 58 and total time for setting test is about 2 minutes
        // so if current min to create flash sale campaign always <= 56 (56 + 2 = 58) with start hour = 23
        // and always <= 57 (57 + 2 = 59) with other start hour
        // when current min exceeds that milestone, current min has been set is 0 and the current hour is increased to 1
        currentMin = (currentHour == 23) ? ((currentMin <= 56) ? currentMin + 2 : 0) : ((currentMin <= 57) ? currentMin + 2 : 0);

        // check current hour, if current hour > 23 after check current min condition, current hour has been set is 0
        // and current day is increased to 1
        currentHour = ((currentMin == 0)) ? (currentHour + 1) : currentHour;
        currentHour = (currentHour > 23) ? 0 : currentHour;

        // check day is increased day or not
        int incDay = (currentHourCheck != 0) && (currentHour == 0) ? 1 : 0;

        // get start time
        if (incDay > 0) {
            return List.of(incDay, currentHour, currentMin);
        } else {
            return List.of(incDay, hour <= currentHour ? currentHour : Math.min(hour, 23), (hour <= currentHour) ? currentMin : Math.min(min, 58));
        }
    }

    private void setTime(int... time) {
        // get start hour
        // if no start hour has provided, start hour = current hour
        startHour = (time.length > 0) ? time[0] : LocalTime.now().getHour();

        // get start min
        // if no start min has provided, start min = current min + 2
        // + 2: total time for the setting is about 2 minutes
        startMin = (time.length > 1) ? time[1] : LocalTime.now().getMinute();

        List<Integer> startTime = getStartTime(startHour, startMin);
        incDay = startTime.get(0);
        startHour = startTime.get(1);
        startMin = startTime.get(2);

        // get end hour
        // if no end hour provided, end hour = start hour + 1 if start min > 58
        // else end hour = start hour
        endHour = (time.length > 2) ? time[2] : ((startMin > 58) ? (startHour + 1) : startHour);

        // get end min
        // if no end min provided, end min = start min + 1
        // if end min > 59 => end min is set to 0
        endMin = (time.length > 3) ? time[3] : ((startMin > 58) ? 0 : (startMin + 1));

        // Set start time
        wait.until(ExpectedConditions.elementToBeClickable(START_HOUR)).click();
        commonAction.waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(startHour))).click();
        logger.info("Set start hour: %s".formatted(startHour));

        wait.until(ExpectedConditions.elementToBeClickable(START_MIN)).click();
        commonAction.waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(startMin))).click();
        logger.info("Set start min: %s".formatted(startMin));

        // Set end time
        wait.until(ExpectedConditions.elementToBeClickable(END_HOUR)).click();
        commonAction.waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(endHour))).click();
        logger.info("Set end hour: %s".formatted(endHour));

        wait.until(ExpectedConditions.elementToBeClickable(END_MIN)).click();
        commonAction.waitElementList(TIME_DROPDOWN);
        wait.until(ExpectedConditions.elementToBeClickable(TIME_DROPDOWN.get(endMin))).click();
        logger.info("Set end min: %s".formatted(endMin));

    }

    private void clickOnTheSaveBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        logger.info("Save flash sale time.");
    }

    public void addNewFlashSaleTime() throws InterruptedException {
        clickAddTimeBtn();
        setTime();
        clickOnTheSaveBtn();
        closeCreateSuccessfullyPopup();
    }
}
