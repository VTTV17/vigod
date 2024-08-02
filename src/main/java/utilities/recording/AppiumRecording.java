package utilities.recording;

import io.appium.java_client.screenrecording.CanRecordScreen;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import utilities.data.DataGenerator;
import utilities.utils.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class AppiumRecording {
    public static void startRecording(WebDriver driver) {
        // Start screen recording
        ((CanRecordScreen) driver).startRecordingScreen();
    }

    @SneakyThrows
    public static void stopRecording(WebDriver driver, ITestResult result){
        // Stop screen recording and get the recorded video
        String video = ((CanRecordScreen) driver).stopRecordingScreen();

        // Save the video to a file when test is failed
        if (!result.isSuccess()) {
            java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
            byte[] decodedBytes = decoder.decode(video);

            // Delete old recording
            String fileName = "%s.mp4".formatted(result.getName());
            new FileUtils().deleteFileInDownloadFolder(fileName);

            // Save recording
            Path path = Paths.get(new DataGenerator().getPathOfFolder("recording_video") + File.separator + fileName);
            Files.write(path, decodedBytes);
        }
    }
}
