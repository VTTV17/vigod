package utilities.extentreports;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static final ExtentReports extentReports = new ExtentReports();

    public synchronized static ExtentReports getExtentReports() {
    	String workingDir = System.getProperty("user.dir");
        ExtentSparkReporter reporter = new ExtentSparkReporter(workingDir + File.separator + "target" + File.separator + "ExtentReport.html");
        reporter.config().setReportName("Extent Report");
        extentReports.attachReporter(reporter);
        extentReports.setSystemInfo("Author", "Automation Tester");
        return extentReports;
    }
}
