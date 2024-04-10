package web.StoreFront;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.excel.Excel;
import utilities.utils.PropertiesUtil;

import java.io.IOException;

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

    @BeforeSuite
    @Parameters({"browser", "headless", "environment", "language"})
    public void getConfig(@Optional("chrome") String browser,
                          @Optional("false") String headless,
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
//        new Screenshot().takeScreenshot(driver);

        // reset count false
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
    void tearDown() {
//        if (driver != null) driver.quit();
    }
}
