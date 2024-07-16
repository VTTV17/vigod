package utilities.model.dashboard.setting.plan;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class PlanStatus {
	Integer id;
	Integer userId;
	String bundlePackagePlanCode;
	String registerPackageDate;
	String expiredPackageDate;
	String bundlePackagePlanName;
	Integer packageId;
	String status;
}