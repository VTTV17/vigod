package api.mailnesia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.api.API;

public class APIMailnesia {
	final static Logger logger = LogManager.getLogger(APIMailnesia.class);
	
	static public void deleteAllEmails(String email) {
		new API("https://mailnesia.com").postDesignatedForMailnesia("/mailbox/%s".formatted(email.replaceAll("\\@mailnesia\\.com", "")), "noTokenNeeded").then().log().all();
		logger.info("Deleted all emails in {}", email);
	}
}
