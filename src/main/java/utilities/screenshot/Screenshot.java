package utilities.screenshot;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.data.DataGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Screenshot {
    public static String generateDateTime(String dateFormat) {
        return DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.now());
    }

    @SneakyThrows
    public void takeScreenshot(WebDriver driver) {
        // Create debug if that not available
        File theDir = new File("./debug/");
        if (!theDir.exists())
            LogManager.getLogger().info(theDir.mkdirs() ? "Create folder 'debug' folder" : "Can not create 'debug' folder");

        String path = new DataGenerator().getPathOfFolder("debug") + "/%s_%s.png".formatted("debug",
                generateDateTime("yyyy_MM_dd-HH_mm_ss")).replace("/", File.separator);
        FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File(path));
    }

    @SneakyThrows
    public void takeScreenshot(WebDriver driver, String folderName, String fileName) {
        // Create debug if that not available
        File theDir = new File("./debug/%s/".formatted(folderName));
        if (!theDir.exists())
            LogManager.getLogger().info(theDir.mkdirs() ? "Create folder '" + folderName + "' folder" : "Can not create '" + folderName + "' folder");

        String path = "./debug/%s/%s.png".formatted(folderName, fileName);
        FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File(path));
    }

    @SneakyThrows
    public Screenshot takeScreenShot(WebElement element) {
        // Get element screenshot
        File screenshot = element.getScreenshotAs(OutputType.FILE);

        // Create recording_video if that not available
        File theDir = new File("./src/main/resources/files/element_image");
        if (!theDir.exists())
            LogManager.getLogger().info(theDir.mkdirs() ? "Create folder 'element_image' folder" : "Can not create 'element_image' folder");

        // Get destination folder
        File destination = new File(new DataGenerator().getPathOfFolderInResourceRoot("element_image") + File.separator + "el_image.png");

        // Move file into destination
        FileUtils.copyFile(screenshot, destination);
        return this;
    }

    @SneakyThrows
    public boolean compareImages() {
        // Load the images
        BufferedImage img1 = ImageIO.read(new File(new DataGenerator().getPathOfFileInResourcesRoot("checked.png")));
        BufferedImage img2 = ImageIO.read(new File(new DataGenerator().getPathOfFileInResourcesRoot("el_image.png")));

        // Compare pixel by pixel
        int totalPixel = img1.getHeight() * img1.getWidth() / 4;
        int matchPixel = 0;
        for (int height = 0; height < img1.getHeight() / 2; height++) {
            for (int width = 0; width < img1.getWidth() / 2; width++) {
                if (img1.getRGB(width, height) == img2.getRGB(width, height)) {
                    matchPixel++;
                }
            }
        }

        return Math.round((float) matchPixel / totalPixel) == 1;
    }

}
