package utilities.model.staffPermission.Cashbook;

import lombok.Data;

@Data
public class Cashbook{
	private boolean createPaymentTransaction;
	private boolean viewReceiptTransactionDetail;
	private boolean editReceiptTransaction;
	private boolean deleteReceiptTransaction;
	private boolean deletePaymentTransaction;
	private boolean editPaymentTransaction;
	private boolean viewPaymentTransactionList;
	private boolean createReceiptTransaction;
	private boolean viewReceiptTransactionList;
	private boolean viewPaymentTransactionDetail;
}