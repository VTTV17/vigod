package utilities.data;

import org.openqa.selenium.By;

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
}
