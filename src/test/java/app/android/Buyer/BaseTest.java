package app.android.Buyer;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.assert_customize.AssertCustomize;
import utilities.recording.AppiumRecording;
import utilities.utils.PropertiesUtil;

import java.io.IOException;

@Listeners(utilities.listeners.ReportListener.class)
public class BaseTest {
    public WebDriver driver;
    public String tcsFileName;
    public String testCaseId;
    public String browser;
    public String headless;
    public String language;

    @BeforeSuite
    public void getConfig() {
        this.browser = PropertiesUtil.browser;
        this.headless = PropertiesUtil.headless;
        this.language = PropertiesUtil. sfLanguage;
    }

    @BeforeMethod
    void startTest() {
        // Start recording
//        AppiumRecording.startRecording(driver);
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        // Clear assert count false
        AssertCustomize.setCountFalse(0);

        // Stop recording
//        AppiumRecording.stopRecording(driver, result);
    }

    @AfterSuite
    void tearDown() {
        // Clear driver
        if (driver != null) driver.quit();
    }
}
