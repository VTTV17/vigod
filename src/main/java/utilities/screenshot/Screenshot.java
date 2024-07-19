package utilities.screenshot;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
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
        String path = new DataGenerator().getFolderPath("debug") + "/%s_%s.png".formatted("debug",
                generateDateTime("yyyy_MM_dd-HH_mm_ss")).replace("/", File.separator);
        FileUtils.copyFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE), new File(path));
    }

    @SneakyThrows
    public Screenshot takeScreenShot(WebDriver driver, WebElement element) {
        // Get entire page screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = ImageIO.read(screenshot);

        // Get the location of element on the page
        Point point = element.getLocation();

        // Get width and height of the element
        int eleWidth = element.getSize().getWidth();
        int eleHeight = element.getSize().getHeight();

        // Crop the entire page screenshot to get only element screenshot
        BufferedImage elementScreenShoot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
        ImageIO.write(elementScreenShoot, "png", screenshot);

        // Copy the element screenshot to disk
        File screenshotLocation = new File(new DataGenerator().getFolderPath("element_image") + File.separator + "el_image.png");
        FileUtils.copyFile(screenshot, screenshotLocation);

        return this;
    }

    @SneakyThrows
    public boolean compareImages() {
        // Load the images
        BufferedImage img1 = ImageIO.read(new File(new DataGenerator().getFilePath("checked.png")));
        BufferedImage img2 = ImageIO.read(new File(new DataGenerator().getFilePath("el_image.png")));

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
