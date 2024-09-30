package utilities.data;

import org.openqa.selenium.By;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetDataByRegex {
    public static double getAmountByRegex(String text){
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(text);
        String matchNumber = "";
        while (matcher.find()) {
            matchNumber = matchNumber + matcher.group();
        }
        return Double.parseDouble(matchNumber);
    }
    public static String normalizeString(String input) {
        // Normalize the string to decompose accented characters
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Remove diacritical marks
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        // Remove spaces
        return withoutAccents.replaceAll("\\s+", "");
    }
}
