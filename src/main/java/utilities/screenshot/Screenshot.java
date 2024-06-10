package utilities.screenshot;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import utilities.data.DataGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.lang.System.getProperty;

public class Screenshot {
    public static String generateDateTime(String dateFormat) {
        return DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.now());
    }

    public void takeScreenshot(WebDriver driver) throws IOException {
        String path = new DataGenerator().getFolderPath("debug")+ "/%s_%s.jpg".formatted("debug",
                generateDateTime("yyyy_MM_dd-HH_mm_ss")).replace("/", File.separator);
        FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File(path));
    }
}
