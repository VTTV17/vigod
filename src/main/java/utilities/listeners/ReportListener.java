package utilities.listeners;

import com.aventstack.extentreports.Status;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utilities.extentreports.ExtentTestManager;

import static java.lang.Thread.sleep;
import static utilities.extentreports.ExtentManager.getExtentReports;

public class ReportListener implements ITestListener {

    private static final Logger Logger = LogManager.getLogger(ReportListener.class);

    public String getTestName(ITestResult result) {
        return result.getTestName() != null ? result.getTestName()
                : result.getMethod().getConstructorOrMethod().getName();
    }

    public String getTestDescription(ITestResult result) {
        return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
    }

    @SneakyThrows
    public WebDriver getDriver(ITestResult iTestResult) {
        return (WebDriver) iTestResult.getTestClass().getRealClass().getField("driver").get(iTestResult.getInstance());
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        Logger.info("Start testing {}", iTestContext.getName());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        Logger.info("End testing {}", iTestContext.getName());
        getExtentReports().flush();
    }

    @SneakyThrows
    @Override
    public void onTestStart(ITestResult iTestResult) {
        Logger.info("[STARTING] {} is starting.", getTestName(iTestResult));
        ExtentTestManager.saveToReport(iTestResult.getName(), iTestResult.getTestName());
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        Logger.info("[PASSED] {} has passed.", getTestName(iTestResult));
        ExtentTestManager.getTest().log(Status.PASS, getTestDescription(iTestResult));
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        Logger.error("[FAILED] {} has failed.", getTestName(iTestResult));
        Logger.debug("Caused by Exception: {}", iTestResult.getThrowable().toString());
        if (getDriver(iTestResult) != null) {
            ExtentTestManager.addScreenShot(Status.FAIL, iTestResult.getThrowable(), getDriver(iTestResult));
        } else {
            ExtentTestManager.getTest().log(Status.FAIL, iTestResult.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        Logger.warn("[SKIPPED] {} is skipped.", getTestName(iTestResult));
        ExtentTestManager.getTest().log(Status.SKIP, getTestName(iTestResult) + " is skipped.");
    }

}
