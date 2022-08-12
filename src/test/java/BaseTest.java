import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utilities.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.screenshot.Screenshot;

import java.io.IOException;

public class BaseTest {
    public WebDriver driver;
    
    DataGenerator generate;
	UICommonAction commonAction;

    @BeforeMethod
    public void setup() throws InterruptedException {
        driver = new InitWebdriver().getDriver("chrome", "true");
        generate = new DataGenerator();
        commonAction = new UICommonAction(driver);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        new Screenshot().takeScreenshot(driver);
        if (driver != null) {
            driver.quit();
        }
    }
}
