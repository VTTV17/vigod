package app.android.Buyer;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.assert_customize.AssertCustomize;
import utilities.excel.Excel;
import utilities.screenshot.Screenshot;
import utilities.utils.PropertiesUtil;

import java.io.IOException;

public class BaseTest {
    public WebDriver driver;
    public String tcsFileName;
    public String testCaseId;
    public String browser;
    public String headless;
    public String language;
    String URL = "http://127.0.0.1:4723/wd/hub";
    String appPackage = "%s";
    String appActivity = "%s.ui.modules.splash.SplashScreenActivity";

    @BeforeSuite
    @Parameters({"browser", "headless", "environment", "language"})
    void getConfig(@Optional("chrome") String browser,
                   @Optional("true") String headless,
                   @Optional("STAG") String environment,
                   @Optional("VIE") String language) {
        this.browser = browser;
        this.headless = headless;
        this.language = language;

        // set environment, language for Properties
        PropertiesUtil.setEnvironment(environment);
        PropertiesUtil.setDBLanguage(language);
        PropertiesUtil.setSFLanguage(language);
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        if ((tcsFileName != null) && (testCaseId != null)) writeResultToExcel(tcsFileName, 0, result, testCaseId);
        new Screenshot().takeScreenshot(driver);
        AssertCustomize.setCountFalse(0);
    }

    public void writeResultToExcel(String fileName, int sheetId, ITestResult result, String testCaseID) throws IOException {
        Excel excel = new Excel();
        int testCaseRow = excel.getRowCellByKey(fileName, sheetId, testCaseID).get(0);
        int resultCellIndex = excel.getCellIndexByCellValue(fileName, sheetId, 0, "Result %s".formatted(language));
        switch (result.getStatus()) {
            case ITestResult.SUCCESS -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "PASS");
            case ITestResult.SKIP -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "SKIP");
            case ITestResult.FAILURE -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "FAIL");
            default -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "OTHER STATUS");
        }
    }

    @AfterSuite
    public void tearDown() {
//        if (driver != null) driver.quit();
    }
}
