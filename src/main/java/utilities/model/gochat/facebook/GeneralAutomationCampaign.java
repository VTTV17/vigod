package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class GeneralAutomationCampaign {
	Integer storeChatId;
	Integer replies;
	String timeType;
	Integer id;
	String pageId;
	String pageName;
	String campaignName;
	String status;
}