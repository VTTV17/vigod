package web.Dashboard;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.excel.Excel;
import utilities.recording.SeleniumRecording;
import utilities.utils.PropertiesUtil;

import java.io.IOException;
import java.lang.reflect.Method;

@Listeners(utilities.listeners.ReportListener.class)
public class BaseTest {
    public WebDriver driver;
    public DataGenerator generate;
    public UICommonAction commonAction;

    public String tcsFileName;
    public String testCaseId;
    public String browser;
    public String headless;
    public String language;
    public String domain;

    @BeforeSuite
    public void getConfig() {
        this.browser = PropertiesUtil.browser;
        this.headless = PropertiesUtil.headless;
        this.language = PropertiesUtil.dbLanguage;
        this.domain = PropertiesUtil.domain;
    }

    @BeforeMethod
    void startTest(Method method) {
//        SeleniumRecording.startRecord(driver, method);
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
//        SeleniumRecording.stopRecord(result);
        AssertCustomize.setCountFalse(0);
    }


    @AfterSuite
    void tearDownWeb() {
        if (driver != null) driver.quit();
    }
}
