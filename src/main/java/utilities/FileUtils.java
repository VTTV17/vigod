package utilities;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtils {
	final static Logger logger = LogManager.getLogger(FileUtils.class);
	
	public static File getLastDownloadedFile(String folder) {
		File dir = new File(folder);
		File[] files = dir.listFiles();
		Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
		File lastModifiedFile = files[0];
		logger.info("Last downloaded file: " + lastModifiedFile);
		return lastModifiedFile;
	}
}
