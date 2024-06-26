package utilities.model.dashboard.setting.packageinfo;

import lombok.Data;
@Data
public class PaymentCompleteInfo {
	String orderId;
	String subscribedPackage;
	String duration;
	String paymentMethod;
	String total;
}