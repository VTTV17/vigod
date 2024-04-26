package utilities.enums.cashbook;

import java.util.Arrays;

import utilities.utils.PropertiesUtil;

public enum CashbookRevenue {
	DEBT_COLLECTION_FROM_SUPPLIER("cashbook.createReceipt.source.debtCollectionFromSupplier"),
	DEBT_COLLECTION_FROM_CUSTOMER("cashbook.createReceipt.source.debtCollectionFromCustomer"),
	PAYMENT_FOR_ORDER("cashbook.createReceipt.source.paymentForOrder"),
	SALE_OF_ASSETS("cashbook.createReceipt.source.saleOfAssets"),
	OTHER_INCOME("cashbook.createReceipt.source.otherIncome");
	
	private final String enumValue;

    private CashbookRevenue(String label) {
        this.enumValue = label;
    }
    
	public static String[] getAllEnumKeys() {
		return Arrays.stream(CashbookRevenue.values()).map(Enum::name).toArray(String[]::new);
	}    
	
    public static String[] getAllEnumValues() {
        return Arrays.stream(CashbookRevenue.values()).map(value -> value.enumValue).toArray(String[]::new);
    }
	
    public static String getTextByLanguage(CashbookRevenue cashbookEnums) {
    	try {
    		return PropertiesUtil.getPropertiesValueByDBLang(cashbookEnums.enumValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
}
