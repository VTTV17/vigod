package utilities.data.testdatagenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import api.Seller.cashbook.OthersGroupAPI;
import api.Seller.customers.APIAllCustomers;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StaffManagement;
import api.Seller.supplier.supplier.APISupplier;
import utilities.data.DataGenerator;
import utilities.enums.DisplayLanguage;
import utilities.enums.cashbook.CashbookExpense;
import utilities.enums.cashbook.CashbookGroup;
import utilities.enums.cashbook.CashbookPaymentMethod;
import utilities.enums.cashbook.CashbookRevenue;
import utilities.model.dashboard.cashbook.UICreateCashbookData;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.ListUtils;

public class CreateCashbookTDG {
	
	static List<String> customerList;
	static List<String> supplierList;
	static List<String> staffList;
	static List<String> othersList;
	static List<String> branchList;
	
    
    public CreateCashbookTDG(LoginInformation credentials) {
        customerList = new APIAllCustomers(credentials).getAllCustomerNames();
        supplierList = new APISupplier(credentials).getAllSupplierNames();
        staffList = new StaffManagement(credentials).getAllStaffNames();
        othersList = new OthersGroupAPI(credentials).getAllOtherGroupNames();
        branchList = new BranchManagement(credentials).getInfo().getActiveBranches();
    }
	
	static String randomSubject(CashbookGroup group) {
		return switch (group) {
			case CUSTOMER: yield ListUtils.getRandomListElement(customerList);
			case SUPPLIER: yield ListUtils.getRandomListElement(supplierList);
			case STAFF: yield ListUtils.getRandomListElement(staffList);
			default: yield ListUtils.getRandomListElement(othersList);
		};
	}

	static String randomBranch() {
		return ListUtils.getRandomListElement(branchList);
	}		
	
	static boolean randomAccountingChecked() {
		return new Random().nextBoolean();
	}		

	static List<CashbookRevenue> revenueSources(CashbookGroup group) {
		if (!group.equals(CashbookGroup.SUPPLIER)) {
			return Arrays.stream(CashbookRevenue.values()).toList();
		}
		return Arrays.stream(CashbookRevenue.values()).filter(name -> name!=CashbookRevenue.DEBT_COLLECTION_FROM_SUPPLIER).toList();
	}
	
	static String[] expenseSources(CashbookGroup group) {
		if (!group.equals(CashbookGroup.SUPPLIER)) {
			return Arrays.stream(CashbookExpense.values()).map(name -> CashbookExpense.getTextByLanguage(name)).toArray(String[]::new);
		}
		return Arrays.stream(CashbookExpense.values()).filter(name -> name!=CashbookExpense.DEBT_COLLECTION_FROM_SELLER).map(name -> CashbookExpense.getTextByLanguage(name)).toArray(String[]::new);
	}

	static List<String> paymentMethodList() {
		return Arrays.stream(CashbookPaymentMethod.values()).map(name -> CashbookPaymentMethod.getTextByLanguage(name)).toList();
	}	
	static String randomPaymentMethod() {
		return ListUtils.getRandomListElement(Arrays.stream(CashbookPaymentMethod.values()).map(name -> CashbookPaymentMethod.getTextByLanguage(name)).toList());
	}	
	
	static String randomAmount() {
		return String.valueOf(Math.round(DataGenerator.generatNumberInBound(1, 50))*10);
	}	
	
	/**
	 * @param dashboardDisplayLang VIE/ENG
	 */
	public static UICreateCashbookData buildReceiptRecord(DisplayLanguage dashboardDisplayLang, CashbookGroup group, CashbookRevenue revenue) {

		return UICreateCashbookData.builder()
				.amount(randomAmount())
				.branchName(randomBranch())
				.groupType(CashbookGroup.getLocalizedText(group))
				.sourceType(CashbookRevenue.getTextByLanguage(revenue))
				.forAccounting(randomAccountingChecked())
				.paymentMethod(randomPaymentMethod())
				.subject(randomSubject(group))
				.build();
	}
	
}