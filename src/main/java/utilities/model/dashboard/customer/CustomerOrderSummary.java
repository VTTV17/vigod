package utilities.model.dashboard.customer;

import java.math.BigDecimal;

import lombok.Data;

/**
 * <p>Used for deserialization of JSON into POJOs.
 * <p>Example base path: /orderservices2/api/customer-orders/store/203833/customerId/4966790/summary.
 * <p>Only DELIVERED orders calculated. Shipping fee is excluded
 */

@Data
public class CustomerOrderSummary {
	Integer totalOrder;
	BigDecimal totalPurchase;
	BigDecimal totalPurchaseLast3Month;
	BigDecimal averangePurchase;
	BigDecimal debtAmount;
	Integer totalReturnOrdersLast3Months;
	Integer totalAmountRefundedLast3Months;
	Integer numberDebtOrder;
	BigDecimal totalRefund;
	BigDecimal totalRefundLast3Months;
	BigDecimal totalRefunded;
	BigDecimal totalRefundedLast3Months;
}