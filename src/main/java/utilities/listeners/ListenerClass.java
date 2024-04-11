package utilities.listeners;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;

public class ListenerClass implements IInvokedMethodListener {
    private boolean hasFailures = false;

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        synchronized (this) {
            if (hasFailures) {
                throw new SkipException("Skipping this test");
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()
                && !testResult.isSuccess()
                && testResult.getThrowable().toString().contains("selenium.TimeoutException")) {
            synchronized (this) {
                hasFailures = true;
            }
        }
    }
}