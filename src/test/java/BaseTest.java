import com.sun.xml.bind.v2.runtime.reflect.opt.FieldAccessor_Boolean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.internal.TestResult;
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

    // PROD config
//    String sellerAccount = "prdvn@nbobd.com";
//    String sellerPassword = "Abc@12345";

    // STG config
    String sellerAccount = "stgauto@nbobd.com";
    String sellerPassword = "Abc@12345";
    String imgFileName = "img.jpg";
    String tcsFileName;
    String testCaseId;

    @BeforeMethod
    public void setup() throws InterruptedException {
        driver = new InitWebdriver().getDriver("chrome","false");
        generate = new DataGenerator();
        commonAction = new UICommonAction(driver);
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        writeResultToExcel(tcsFileName, 0, result, testCaseId);
        if (driver != null) {
            driver.quit();
        }
    }
    public void writeResultToExcel(String fileName, int sheetId,ITestResult result, String testCaseID) throws IOException {
        Excel excel = new Excel();
        int testCaseRow = excel.getRowCellByKey(fileName,sheetId,testCaseID).get(0);
        int resultCellIndex = excel.getCellIndexByCellValue(fileName,sheetId,0,"Result");
        switch (result.getStatus()){
            case ITestResult.SUCCESS ->
                    excel.writeCellValue(fileName,sheetId,testCaseRow,resultCellIndex,"PASS");
            case ITestResult.SKIP ->
                    excel.writeCellValue(fileName,sheetId,testCaseRow,resultCellIndex,"SKIP");
           case ITestResult.FAILURE ->
                   excel.writeCellValue(fileName,sheetId,testCaseRow,resultCellIndex,"FAIL");
           default ->  excel.writeCellValue(fileName,sheetId,testCaseRow,resultCellIndex,"OTHER STATUS");
        }
    }
}
