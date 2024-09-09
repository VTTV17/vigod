package web.StoreFront;

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
    @Parameters({"browser", "headless", "environment", "language", "domain"})
    public void getConfig(@Optional("chrome") String browser,
                          @Optional("true") String headless,
                          @Optional("PREPROD") String environment,
                          @Optional("ENG") String language,
                          @Optional("VN") String domain ) { // either VN or BIZ
        this.browser = browser;
        this.headless = headless;
        this.language = language;
        this.domain = domain;
        // set environment, language for Properties
        PropertiesUtil.setEnvironment(environment);
        PropertiesUtil.setDBLanguage(language);
        PropertiesUtil.setSFLanguage(language);
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
    void tearDownWeb() {
        if (driver != null) driver.quit();
    }
}