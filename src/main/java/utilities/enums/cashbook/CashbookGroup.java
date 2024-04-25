package utilities.enums.cashbook;

import java.util.Arrays;

import utilities.utils.PropertiesUtil;

public enum CashbookGroup {
	OTHERS("cashbook.createReceipt.group.others"),
	CUSTOMER("cashbook.createReceipt.group.customer"),
	STAFF("cashbook.createReceipt.group.staff"),
	SUPPLIER("cashbook.createReceipt.group.supplier");
	
    private final String enumValue;

    private CashbookGroup(String enumValue) {
        this.enumValue = enumValue;
    }
    
	public static String[] getAllEnumKeys() {
		return Arrays.stream(CashbookGroup.values()).map(Enum::name).toArray(String[]::new);
	}    
	
    public static String[] getAllEnumValues() {
        return Arrays.stream(CashbookGroup.values()).map(value -> value.enumValue).toArray(String[]::new);
    }
	
    public static String getTextByLanguage(CashbookGroup cashbookEnums) {
    	try {
    		return PropertiesUtil.getPropertiesValueByDBLang(cashbookEnums.enumValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }

}
