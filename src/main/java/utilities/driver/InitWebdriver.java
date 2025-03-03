package utilities.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import utilities.file.FileNameAndPath;

import java.util.HashMap;
import java.util.Map;

public class InitWebdriver {

    private WebDriver driver;

    public WebDriver getDriver(String browser, String... headlessMode) {
        LogManager.getLogger(System.getProperty("os.name"));
        boolean headless = System.getProperty("os.name").equals("Linux") || headlessMode.length == 0 || headlessMode[0].equals("true");
        if (driver == null) {
            switch (browser) {
                case "firefox" -> {
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (headless) firefoxOptions.addArguments("--headless");
                    firefoxOptions.addArguments("start-maximized");
                    firefoxOptions.addArguments("--no-sandbox");
                    driver = new FirefoxDriver(firefoxOptions);
                }
                case "edge" -> {
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (headless) edgeOptions.addArguments("--headless");
                    edgeOptions.addArguments("start-maximized");
                    edgeOptions.addArguments("--no-sandbox");
                    driver = new EdgeDriver(edgeOptions);
                }
                case "safari" -> {
                    WebDriverManager.safaridriver().setup();
                    driver = new SafariDriver();
                }
                default -> {
                	WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--start-maximized");
                    if (headless) chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--disable-site-isolation-trials");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--no-sandbox");
                    Map<String, Object> prefs = new HashMap<>();
                    prefs.put("download.default_directory", FileNameAndPath.downloadFolder);
                    chromeOptions.setExperimentalOption("prefs", prefs);
                    driver = new ChromeDriver(chromeOptions);
                    if (headless) driver.manage().window().setSize(new Dimension(1920, 1080));
                }
            }
            driver.manage().window().maximize();
        }
        return driver;
    }
}
