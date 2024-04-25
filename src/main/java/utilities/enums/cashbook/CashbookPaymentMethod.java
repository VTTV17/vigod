package utilities.enums.cashbook;

import java.util.Arrays;

import utilities.utils.PropertiesUtil;

public enum CashbookPaymentMethod {
	VISA("cashbook.paymentMethod.visa"),
	ATM("cashbook.paymentMethod.atm"),
	BANK_TRANSFER("cashbook.paymentMethod.bankTransfer"),
	CASH("cashbook.paymentMethod.cash"),
	ZALO("cashbook.paymentMethod.zalopay"),
	MOMO("cashbook.paymentMethod.momo");
//	POS("notyetDefined"),
//	PAYPAL("notyetDefined");
	
	private final String enumValue;

    private CashbookPaymentMethod(String label) {
        this.enumValue = label;
    }
    
	public static String[] getAllEnumKeys() {
		return Arrays.stream(CashbookPaymentMethod.values()).map(Enum::name).toArray(String[]::new);
	}    
	
    public static String[] getAllEnumValues() {
        return Arrays.stream(CashbookPaymentMethod.values()).map(value -> value.enumValue).toArray(String[]::new);
    }
	
    public static String getTextByLanguage(CashbookPaymentMethod cashbookEnums) {
    	try {
    		return PropertiesUtil.getPropertiesValueByDBLang(cashbookEnums.enumValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
}
