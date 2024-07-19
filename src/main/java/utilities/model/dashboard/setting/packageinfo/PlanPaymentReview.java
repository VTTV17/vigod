package utilities.model.dashboard.setting.packageinfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class PlanPaymentReview {
	String name;
	String duration;
	String basePrice;
	String vatPrice;
	String refundAmount;
	String finalTotal;
}