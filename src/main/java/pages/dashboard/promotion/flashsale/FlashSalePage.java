package pages.dashboard.promotion.flashsale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.promotion.flashsale.campaign.CampaignPage;
import pages.dashboard.promotion.flashsale.time.ManageTimePage;

import java.time.Duration;
import java.util.List;

import static java.lang.Thread.sleep;

public class FlashSalePage extends FlashSaleElement {
    WebDriverWait wait;
    public int startHour;
    public int startMin;

    public int incDay;

    public FlashSalePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    Logger logger = LogManager.getLogger(FlashSalePage.class);

    public FlashSalePage navigate() throws InterruptedException {
        new HomePage(driver).hideFacebookBubble().navigateToPromotion_FlashSalePage();
        logger.info("Current page is %s".formatted(driver.getTitle()));
        return this;
    }

    private List<Integer> checkTime(int Hour, int Min) {
        int incDay = 0;
        if (Min + 2 <= 58) {
            Min = Min + 2;
        } else {
            Min = 0;
            Hour++;
        }
        if (Hour > 23) {
            Hour = 0;
            incDay++;
        }

        return List.of(incDay, Hour, Min);
    }

    public FlashSalePage setFlashSaleTime(int startHour, int startMin) throws InterruptedException {
        List<Integer> timeSet = checkTime(startHour, startMin);
        incDay = timeSet.get(0);
        this.startHour = timeSet.get(1);
        this.startMin = timeSet.get(2);

        sleep(1000);
        if (driver.getCurrentUrl().contains("intro")) {
            wait.until(ExpectedConditions.visibilityOf(EXPLORE_NOW_BTN)).click();
            logger.info("Skip Flash sale intro");
        }

        wait.until(ExpectedConditions.elementToBeClickable(MANAGE_FLASH_SALE_TIME_BTN)).click();
        logger.info("Click on the Manage flash sale time button");
        new ManageTimePage(driver).addNewFlashSaleTime();
        return this;
    }

    public void createFlashSaleCampaign(String campaignName, String day, int price, String currency, int quantity, String... productName) throws InterruptedException {
        new HomePage(driver).navigateToPromotion_FlashSalePage();
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_CAMPAIGN_BTN)).click();
        new CampaignPage(driver).verifyPageLoaded().setCampaign(campaignName, day, incDay, startHour, startMin, price, currency, quantity, productName);
    }
}
