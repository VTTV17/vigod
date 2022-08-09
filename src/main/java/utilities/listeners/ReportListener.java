package utilities.listeners;

import utilities.extentreports.ExtentTestManager;
import com.aventstack.extentreports.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import static utilities.extentreports.ExtentManager.getExtentReports;

public class ReportListener implements ITestListener {
	
	private static final Logger Logger =  LogManager.getLogger(ReportListener.class);
	WebDriver driver = null;

    public String getTestName(ITestResult result) {
        return result.getTestName() != null ? result.getTestName()
                : result.getMethod().getConstructorOrMethod().getName();
    }

    public String getTestDescription(ITestResult result) {
        return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
    }
    
    public WebDriver getDriver(ITestResult iTestResult) {
        try {
			driver= (WebDriver)iTestResult.getTestClass().getRealClass().getField("driver").get(iTestResult.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return driver;
    }
    
    @Override
    public void onStart(ITestContext iTestContext) {
    	Logger.info("Start testing " + iTestContext.getName());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
    	Logger.info("End testing " + iTestContext.getName());
        getExtentReports().flush();
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
    	Logger.info("[STARTING] " + getTestName(iTestResult) + " is starting.");
        ExtentTestManager.saveToReport(iTestResult.getName(), iTestResult.getTestName());
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
    	Logger.info("[PASSED] " + getTestName(iTestResult) + " has passed.");
    	ExtentTestManager.getTest().log(Status.PASS, getTestDescription(iTestResult));
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
    	Logger.error("[FAILED] " + getTestName(iTestResult) + " has failed.");
    	Logger.debug("Caused by Exception: " + iTestResult.getThrowable());
        if (getDriver(iTestResult) != null) {
        	ExtentTestManager.addScreenShot(Status.FAIL, iTestResult.getThrowable(), getDriver(iTestResult));
        } else {
        	ExtentTestManager.getTest().log(Status.FAIL, iTestResult.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
    	Logger.warn("[SKIPPED] " + getTestName(iTestResult) + " is skipped.");
    	ExtentTestManager.getTest().log(Status.SKIP, getTestName(iTestResult) + " is skipped.");
    }

}
