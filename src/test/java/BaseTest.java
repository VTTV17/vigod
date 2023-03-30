import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import utilities.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.excel.Excel;
import utilities.screenshot.Screenshot;

import java.io.IOException;

public class BaseTest {
    public WebDriver driver;
    DataGenerator generate;
    UICommonAction commonAction;

    String tcsFileName;
    String testCaseId;

    @BeforeMethod
    @Parameters()
    public void setup() throws InterruptedException {
        if (driver == null) driver = new InitWebdriver().getDriver("chrome", "true");
        generate = new DataGenerator();
        commonAction = new UICommonAction(driver);
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        if ((tcsFileName != null) && (testCaseId != null)) writeResultToExcel(tcsFileName, 0, result, testCaseId);
        new Screenshot().takeScreenshot(driver);
    }

    public void writeResultToExcel(String fileName, int sheetId, ITestResult result, String testCaseID) throws IOException {
        Excel excel = new Excel();
        int testCaseRow = excel.getRowCellByKey(fileName, sheetId, testCaseID).get(0);
        int resultCellIndex = excel.getCellIndexByCellValue(fileName, sheetId, 0, "Result");
        switch (result.getStatus()) {
            case ITestResult.SUCCESS -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "PASS");
            case ITestResult.SKIP -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "SKIP");
            case ITestResult.FAILURE -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "FAIL");
            default -> excel.writeCellValue(fileName, sheetId, testCaseRow, resultCellIndex, "OTHER STATUS");
        }
    }

    @AfterSuite
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
