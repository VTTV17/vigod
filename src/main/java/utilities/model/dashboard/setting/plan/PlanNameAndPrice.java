package utilities.model.dashboard.setting.plan;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class PlanNameAndPrice {
	String bundlePackagePlanName;
	String bundlePackagePlanCode;
	BigDecimal monthPrice;
	List<BigDecimal> totalPrice;
	String currency;
}