package utilities.enums.cashbook;

import java.util.Arrays;

import utilities.utils.PropertiesUtil;

public enum CashbookExpense {
	PAYMENT_TO_SHIPPING_PARTNER("cashbook.createPayment.expense.paymentToShippingPartner"),
	PAYMENT_FOR_GOODS("cashbook.createPayment.expense.paymentForGoods"),
	PRODUCTION_COST("cashbook.createPayment.expense.productionCost"),
	COST_OF_RAW_MATERIALS("cashbook.createPayment.expense.costOfRawMaterials"),
	DEBT_COLLECTION_FROM_SELLER("cashbook.createPayment.expense.debtPaymentToCustomer"),
	RENTAL_FEE("cashbook.createPayment.expense.rentalFee"),
	UTILITIES("cashbook.createPayment.expense.utilities"),
	SALARIES("cashbook.createPayment.expense.salaries"),
	SELLING_EXPENSES("cashbook.createPayment.expense.sellingExpenses"),
	OTHER_COSTS("cashbook.createPayment.expense.otherCosts"),
	REFUND("cashbook.createPayment.expense.refund");
	
	private final String enumValue;

    private CashbookExpense(String label) {
        this.enumValue = label;
    }
    
	public static String[] getAllEnumKeys() {
		return Arrays.stream(CashbookExpense.values()).map(Enum::name).toArray(String[]::new);
	}    
	
    public static String[] getAllEnumValues() {
        return Arrays.stream(CashbookExpense.values()).map(value -> value.enumValue).toArray(String[]::new);
    }
	
    public static String getTextByLanguage(CashbookExpense cashbookEnums) {
    	try {
    		return PropertiesUtil.getPropertiesValueByDBLang(cashbookEnums.enumValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
}
