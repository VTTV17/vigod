package web.Dashboard.products.inventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.inventory.history.InventoryHistoryPage;

public class InventoryPage extends InventoryElement {
    WebDriver driver;
    UICommonAction commons;

    final static Logger logger = LogManager.getLogger(InventoryPage.class);

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
    }



    public InventoryPage navigate() {
        new HomePage(driver).navigateToPage("Products", "Inventory");
        return this;
    }

    public InventoryHistoryPage clickInventoryHistory() {
        commons.click(loc_btnInventoryHistory);
        logger.info("Clicked on 'Inventory History' button.");
        new HomePage(driver).waitTillSpinnerDisappear1();
        return new InventoryHistoryPage(driver);
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToSeeInventoryHistory(String permission, String url) {
        clickInventoryHistory();
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commons.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }


    /*-------------------------------------*/

}
