package utilities.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatDate {
	
	/**
	 * 
	 * @param inputFormat Eg. dd/MM/yyyy
	 * @param desiredFormat Eg. yyyy-MM-dd
	 * @param dateToFormat Eg. 15/02/2023
	 * @return
	 */
    public String formatDate(String inputFormat, String desiredFormat, String dateToFormat) {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
		SimpleDateFormat desiredDateFormat = new SimpleDateFormat(desiredFormat);
		String reformattedStr = null;
		try {
			reformattedStr = desiredDateFormat.format(inputDateFormat.parse(dateToFormat));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return reformattedStr;
    }
    
    /**
     * 
     * @param inputFormat Eg. dd/MM/yyyy
     * @param dateToConvert
     * @return
     */
    public Date convertStringToDate(String inputFormat, String dateToConvert) {
    	SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
    	Date convertedDate = null;
    	try {
    		convertedDate = inputDateFormat.parse(dateToConvert);
    	} catch (ParseException e) {
    		e.printStackTrace();
    	}
    	return convertedDate;
    }

}
