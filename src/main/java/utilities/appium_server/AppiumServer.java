package utilities.appium_server;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.cmd.CommandWindows;
import utilities.get_port.FreePort;

public class AppiumServer {
    private static final Logger logger = LogManager.getLogger();

    @Getter
    private static int appiumServerPort;

    public static void startServer() {
        // Get appium server port
        appiumServerPort = FreePort.get();

        // Start appium server
        CommandWindows.execute("appium -a 0.0.0.0 -p %s -pa /wd/hub --allow-cors".formatted(appiumServerPort));

        // Log
        logger.info("Start appium server with port: {}", appiumServerPort);
    }

    public static void stopServer() {
        // Stop appium server
        CommandWindows.killAllCommandWindows();

        // Log
        logger.info("Stop appium server.");
    }
}
