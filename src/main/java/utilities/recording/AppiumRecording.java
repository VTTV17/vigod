package utilities.recording;

import io.appium.java_client.screenrecording.CanRecordScreen;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import utilities.data.DataGenerator;
import utilities.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;


public class AppiumRecording {
    public static void startRecording(WebDriver driver) {
        // Start screen recording
        ((CanRecordScreen) driver).startRecordingScreen();
    }

    @SneakyThrows
    public static void stopRecording(WebDriver driver, ITestResult result) {
        // Stop screen recording and get the recorded video
        String video = ((CanRecordScreen) driver).stopRecordingScreen();

        // Save the video to a file when the test fails
        if (!result.isSuccess()) {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedBytes = decoder.decode(video);

            // Create recording_video directory if it doesn't exist
            Path videoDir = Paths.get("recording_video");
            if (!Files.exists(videoDir)) {
                boolean created = new File(videoDir.toString()).mkdirs();
                LogManager.getLogger().info(created ? "Created 'recording_video' folder" : "Could not create 'recording_video' folder");
            }

            // Generate unique file name
            String fileName = "%s_%d.mp4".formatted(result.getName(), System.currentTimeMillis());
            Path videoPath = videoDir.resolve(fileName);

            // Write video file
            try {
                Files.write(videoPath, decodedBytes);
                LogManager.getLogger().info("Saved video recording: {}", videoPath);
            } catch (IOException e) {
                LogManager.getLogger().error("Failed to save video recording", e);
            }
        }
    }
}
