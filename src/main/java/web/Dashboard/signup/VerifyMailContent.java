package web.Dashboard.signup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import utilities.model.dashboard.setting.packageinfo.PaymentCompleteInfo;
import utilities.model.dashboard.setupstore.SetupStoreDG;

public class VerifyMailContent {

	final static Logger logger = LogManager.getLogger(VerifyMailContent.class);
	
    static String DOMAIN_VN = "gosell.vn";
    static String DOMAIN_BIZ = "gosell.biz";
    static String GOSELLPHONE = "02873030800";
    static String GOSELLPHONE1 = "(028) 7303 0800";
    static String GOSELL_LOCAL_BANKNUMBER = "04201015009138";
    static String GOSELL_FOREIGN_BANKNUMBER = "0331370480531";
    
    
    static String VERIFICATION_CODE_REGEX = ".{3,7}:\\n(\\d+)\\n";
    static String GREETING_REGEX = "(?:[^\\?][Cc]h.o|Hello|Hi)\\s?(.*),";
    static String GOSELLMAIL_REGEX = "(?:<mailto:)?(.*\\@.*\\..{2,3})(?:\\?|or|.{3}c)";
    static String GOSELLPHONE_REGEX = "(?:<tel:|\\n)(\\d{8,})";
    static String TRACKINGLINK_REGEX = "//tracking\\.(.*)/tracking";
    static String ORDERID_REGEX = "(?:Order ID|M. đ.n h.ng):#(\\d+)";
    
    static String getDomain(String url) {
    	return url.contains("biz") ? DOMAIN_BIZ : DOMAIN_VN;
    }
    static String getGoSellEmail(String url) {
    	String subMail = url.contains("biz") ? "support@" : "hotro@";
    	return subMail + getDomain(url);
    }
    static String getGoSellAccountNumber(String country) {
    	return country.contentEquals("Vietnam") ? GOSELL_LOCAL_BANKNUMBER : GOSELL_FOREIGN_BANKNUMBER;
    }

    public static List<String> extractInfoFromMailBody(String content, String regex) {
    	
    	Matcher matcher = Pattern.compile(regex).matcher(content);
    	
    	List<String> results = new ArrayList<String>();
		while (matcher.find()) {
			results.add(matcher.group(1));
			System.out.println(matcher.group(1));
		}
    	return results;
    }      
    
    public static void verificationCode(String content, SetupStoreDG store) {
    	
    	List<String> code = extractInfoFromMailBody(content, VERIFICATION_CODE_REGEX);
    	Assert.assertEquals(code.size(), 1);
    	Assert.assertTrue(code.get(0).matches("\\d{6}"));
    	
    	List<String> email = extractInfoFromMailBody(content, GOSELLMAIL_REGEX);
    	Assert.assertEquals(email.size(), 2);
    	email.stream().forEach(e -> Assert.assertEquals(e, getGoSellEmail(store.getDomain())));
    	
    	List<String> phone = extractInfoFromMailBody(content, GOSELLPHONE_REGEX);
    	Assert.assertEquals(phone.size(), 2);
    	phone.stream().forEach(e -> Assert.assertEquals(e, GOSELLPHONE));
    	
    	List<String> urlLinkText = extractInfoFromMailBody(content, TRACKINGLINK_REGEX);
    	Assert.assertEquals(urlLinkText.size(), 3);
    	urlLinkText.stream().forEach(e -> Assert.assertEquals(e, getDomain(store.getDomain())));
    }
    public static void successfulAccountRegistration(String content, SetupStoreDG store) {
    	
    	List<String> temporaryDisplayName = extractInfoFromMailBody(content, GREETING_REGEX);
    	Assert.assertEquals(temporaryDisplayName.size(), 1);
    	Assert.assertTrue(temporaryDisplayName.get(0).contentEquals(store.getUsername().split("\\@")[0]));
    	
    	List<String> username = extractInfoFromMailBody(content, "(?:kho.n|registered)(.*)(?:tr.n|on) GoSell\\.");
    	Assert.assertEquals(username.size(), 1);
    	username.stream().forEach(e -> Assert.assertEquals(e, store.getUsername()));
    	
    	List<String> email = extractInfoFromMailBody(content, GOSELLMAIL_REGEX);
    	Assert.assertEquals(email.size(), 2);
    	email.stream().forEach(e -> Assert.assertEquals(e, getGoSellEmail(store.getDomain())));
    	
    	List<String> phone = extractInfoFromMailBody(content, GOSELLPHONE_REGEX);
    	Assert.assertEquals(phone.size(), 2);
    	phone.stream().forEach(e -> Assert.assertEquals(e, GOSELLPHONE));
    	
    	List<String> urlLinkText = extractInfoFromMailBody(content, TRACKINGLINK_REGEX);
    	Assert.assertEquals(urlLinkText.size(), 4);
    	urlLinkText.stream().forEach(e -> Assert.assertEquals(e, getDomain(store.getDomain())));
    } 
    public static void welcome(String content, SetupStoreDG store) {
    	
    	List<String> temporaryDisplayName = extractInfoFromMailBody(content, GREETING_REGEX);
    	Assert.assertEquals(temporaryDisplayName.size(), 1);
    	Assert.assertTrue(temporaryDisplayName.get(0).contentEquals(store.getUsername().split("\\@")[0]));
    	
    	List<String> username = extractInfoFromMailBody(content, "(?:registered your account|email :) ([^\\s]+)\\s?");
    	Assert.assertEquals(username.size(), 1);
    	username.stream().forEach(e -> Assert.assertEquals(e, store.getEmail()));
    	
    	List<String> email = extractInfoFromMailBody(content, "(?:<mailto:)?(.*\\@gosell\\.[^>\\n]{2,3})");
    	Assert.assertEquals(email.size(), 2);
    	email.stream().forEach(e -> Assert.assertEquals(e, getGoSellEmail(store.getDomain())));
    	
    	List<String> phone = extractInfoFromMailBody(content, ": (\\(\\d+\\) \\d+ \\d+)");
    	Assert.assertEquals(phone.size(), 1);
    	phone.stream().forEach(e -> Assert.assertEquals(e, GOSELLPHONE1));
    	
    	List<String> urlLinkText = extractInfoFromMailBody(content, TRACKINGLINK_REGEX);
    	Assert.assertEquals(urlLinkText.size(), 3);
    	urlLinkText.stream().forEach(e -> Assert.assertEquals(e, getDomain(store.getDomain())));
    }    
    public static void paymentConfirmation(String content, SetupStoreDG store, PaymentCompleteInfo paymentCompleteInfo) {
    	
    	List<String> orderId = extractInfoFromMailBody(content, ORDERID_REGEX);
    	Assert.assertEquals(orderId.size(), 1);
    	orderId.stream().forEach(e -> Assert.assertEquals(e, paymentCompleteInfo.getOrderId()));
    	
    	List<String> accountNumber = extractInfoFromMailBody(content, "(?:Account number|S. t.i kho.n):(\\d+)");
    	Assert.assertEquals(accountNumber.size(), 1);
    	accountNumber.stream().forEach(e -> Assert.assertEquals(e, getGoSellAccountNumber(store.getCountry())));
    	
    	List<String> orderTotal = extractInfoFromMailBody(content, "(?:Total cost|T.ng chi ph.):(.+)");
    	Assert.assertEquals(orderTotal.size(), 1);
    	orderTotal.stream().forEach(e -> Assert.assertEquals(e.replaceAll("\\s", ""), paymentCompleteInfo.getTotal())); //Remove the space in 20,900,000 đ
    	
    	List<String> email = extractInfoFromMailBody(content, GOSELLMAIL_REGEX);
    	Assert.assertEquals(email.size(), 2);
    	email.stream().forEach(e -> Assert.assertEquals(e, getGoSellEmail(store.getDomain())));
    	
    	List<String> phone = extractInfoFromMailBody(content, GOSELLPHONE_REGEX);
    	Assert.assertEquals(phone.size(), 2);
    	phone.stream().forEach(e -> Assert.assertEquals(e, GOSELLPHONE));
    	
    	List<String> urlLinkText = extractInfoFromMailBody(content, TRACKINGLINK_REGEX);
    	Assert.assertEquals(urlLinkText.size(), 2);
    	urlLinkText.stream().forEach(e -> Assert.assertEquals(e, getDomain(store.getDomain())));
    }    
    
    public static void successfulPayment(String content, SetupStoreDG store, PaymentCompleteInfo paymentCompleteInfo, String expectedExpiryDate) {
    	List<String> orderId = extractInfoFromMailBody(content, ORDERID_REGEX);
    	Assert.assertEquals(orderId.size(), 1);
    	orderId.stream().forEach(e -> Assert.assertEquals(e, paymentCompleteInfo.getOrderId()));
    	
    	List<String> serviceName = extractInfoFromMailBody(content, "(?:Service|D.ch v.):([A-Z+]+)(?:Expiry|Ng)");
    	Assert.assertEquals(serviceName.size(), 1);
    	serviceName.stream().forEach(e -> Assert.assertEquals(e, paymentCompleteInfo.getSubscribedPackage()));
    	
    	List<String> expiryDate = extractInfoFromMailBody(content, "(?:Expiry date|Ng.y h.t h.n):(\\d{2}/\\d{2}/\\d{4})");
    	Assert.assertEquals(expiryDate.size(), 1);
    	expiryDate.stream().forEach(e -> Assert.assertEquals(e, expectedExpiryDate));
    	
    	List<String> email = extractInfoFromMailBody(content, GOSELLMAIL_REGEX);
    	Assert.assertEquals(email.size(), 2);
    	email.stream().forEach(e -> Assert.assertEquals(e, getGoSellEmail(store.getDomain())));
    	
    	List<String> phone = extractInfoFromMailBody(content, GOSELLPHONE_REGEX);
    	Assert.assertEquals(phone.size(), 2);
    	phone.stream().forEach(e -> Assert.assertEquals(e, GOSELLPHONE));
    	
    	List<String> urlLinkText = extractInfoFromMailBody(content, TRACKINGLINK_REGEX);
    	Assert.assertEquals(urlLinkText.size(), 2);
    	urlLinkText.stream().forEach(e -> Assert.assertEquals(e, getDomain(store.getDomain())));
    }      
    
}
