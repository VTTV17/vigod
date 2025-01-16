package utilities.enums.cashbook;

import java.util.Arrays;
import java.util.List;

import utilities.enums.Domain;
import utilities.utils.PropertiesUtil;

public enum CashbookPaymentMethod {
	VISA("cashbook.paymentMethod.visa"),
	ATM("cashbook.paymentMethod.atm"),
	BANK_TRANSFER("cashbook.paymentMethod.bankTransfer"),
	CASH("cashbook.paymentMethod.cash"),
	ZALO("cashbook.paymentMethod.zalopay"),
	MOMO("cashbook.paymentMethod.momo"),
	MPOS("cashbook.paymentMethod.mpos");
//	PAYPAL("notyetDefined");
	
	private final String enumValue;

    private CashbookPaymentMethod(String label) {
        this.enumValue = label;
    }
    
//	public static String[] getAllEnumKeys() {
//		return Arrays.stream(CashbookPaymentMethod.values()).map(Enum::name).toArray(String[]::new);
//	}    
	
//    public static String[] getAllEnumValues() {
//        return Arrays.stream(CashbookPaymentMethod.values()).map(value -> value.enumValue).toArray(String[]::new);
//    }
	
    public static String getTextByLanguage(CashbookPaymentMethod cashbookEnums) {
    	try {
    		return PropertiesUtil.getPropertiesValueByDBLang(cashbookEnums.enumValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static List<CashbookPaymentMethod> availablePaymentListByDomain(Domain domain) {
    	if (domain.equals(Domain.VN)) {
    		return Arrays.asList(CashbookPaymentMethod.values());
    	}
    	return Arrays.asList(CASH, BANK_TRANSFER);
    }
    
}
