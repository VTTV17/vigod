package utilities.utils;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.data.DataGenerator;
import utilities.file.FileNameAndPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {
    final static Logger logger = LogManager.getLogger(FileUtils.class);

    public static File getLastDownloadedFile(String folder) {
        File dir = new File(folder);
        File[] files = dir.listFiles();
        assert files != null;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        File lastModifiedFile = files[0];
        logger.info("Last downloaded file: " + lastModifiedFile);
        return lastModifiedFile;
    }

    public boolean isDownloadSuccessful(String fileName) {
        File directory = new File(FileNameAndPath.downloadFolder);
        File[] files = directory.listFiles();
        return files != null && Arrays.stream(files).anyMatch(file -> file.getName().contains(fileName));
    }

    public void deleteFileInDownloadFolder(String subName) {
        File directory = new File(FileNameAndPath.downloadFolder);
        File[] files = directory.listFiles();
        if (files != null)
            Arrays.stream(files)
                    .filter(file -> file.getName().contains(subName))
                    .map(file -> file.delete()
                            ? "File '%s' is deleted.".formatted(file.getName())
                            : "Something went wrong when delete '%s' file.".formatted(file.getName()))
                    .forEachOrdered(logger::info);
    }


    public void deleteFile(String fileName) throws IOException {
        File theDir = new File("./recording_video/" + fileName);
        if (theDir.exists())
            Files.delete(Paths.get(System.getProperty("user.dir") + "/recording_video/" + fileName));
    }
}
