package utilities.recording;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import utilities.screenshot.Screenshot;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SeleniumRecording {
    public static ScheduledExecutorService executorService;

    // Start recording video
    @SneakyThrows
    public static void startRecord(WebDriver driver, Method method) {
        // Delete old video
        new utilities.utils.FileUtils().deleteFile(method.getName() + ".mp4");

        // Create debug if that not available
        File theDir = new File("./debug/%s/".formatted(method.getName()));
        if (!theDir.exists())
            LogManager.getLogger().info(theDir.mkdirs() ? "Create folder '" + method.getName() + "' folder" : "Can not create '" + method.getName() + "' folder");

        // Start the executor service to capture screenshots every 200ms
        executorService = Executors.newScheduledThreadPool(1);
        AtomicInteger index = new AtomicInteger();
        executorService.scheduleAtFixedRate(() -> {
            new Screenshot().takeScreenshot(driver, method.getName(), method.getName() + index);
            index.getAndIncrement();
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    public static void stopRecord(ITestResult iTestResult){
        // End thread
        executorService.shutdown();

        // Create video from images if testcase is failed
        if (!iTestResult.isSuccess())
            createVideoFromImages(iTestResult);

        // Remove images folder
        File[] files = new File(System.getProperty("user.dir") + "/debug/" + iTestResult.getName()).listFiles();
        if (files != null) Arrays.stream(files).parallel().forEach(File::delete);
//        FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/debug/" + iTestResult.getName()));
    }

    private static void createVideoFromImages(ITestResult iTestResult) throws Exception {
        // Build video from images
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-framerate", "5", "-i", System.getProperty("user.dir") + "/debug/" + iTestResult.getName() + "/" + iTestResult.getName() + "%d.png",
                "-c:v", "libx264", "-pix_fmt", "yuv420p", "-movflags", "+faststart",
                "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2", System.getProperty("user.dir") + "/recording_video/%s.mp4".formatted(iTestResult.getName()));
        Process process = processBuilder.start();
        process.waitFor(30, TimeUnit.SECONDS);
    }
}
