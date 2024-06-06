package utilities.udid;

import utilities.data.DataGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DevicesUDID {
    public String get() throws IOException {
        StringBuilder result = new StringBuilder();

        new BufferedReader(new InputStreamReader(new ProcessBuilder().command("cmd.exe", "/c", "adb devices").start().getInputStream())).lines().forEach(line -> result.append(line).append("\n"));

        return DataGenerator.getStringByRegex(result.toString(), "(.*)\\tdevice");
    }
}
