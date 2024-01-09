package utilities.model.staffPermission.Customer;

import lombok.Data;

@Data
public class CustomerManagement{
	private boolean printBarcode;
	private boolean viewAllCustomerList;
	private boolean mergeCustomer;
	private boolean assignPartner;
	private boolean exportCustomer;
	private boolean addCustomer;
	private boolean customerAnalytics;
	private boolean viewCustomerActivity;
	private boolean viewCustomerGeneralInformation;
	private boolean viewAssignedCustomerList;
	private boolean confirmPayment;
	private boolean editCustomerInformation;
	private boolean viewCustomerBankInformation;
	private boolean updateStatus;
	private boolean deleteCustomer;
	private boolean assignStaff;
	private boolean downloadExportedCustomer;
	private boolean importCustomer;
}