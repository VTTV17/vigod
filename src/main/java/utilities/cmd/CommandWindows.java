package utilities.cmd;

import lombok.SneakyThrows;

import static java.lang.Thread.sleep;

public class CommandWindows {
    @SneakyThrows
    public static void execute(String command) {
        // Execute command
        Runtime.getRuntime().exec("cmd.exe /c start cmd.exe /k \"%s\"".formatted(command));

        // Wait command executed
        sleep(3000);
    }

    public static void killAllCommandWindows() {
        // Kill child process
        execute("taskkill /F /IM adb.exe");
        execute("taskkill /F /IM node.exe");

        // Kill all cmd commands
        execute("taskkill /F /IM cmd.exe");
    }
}
