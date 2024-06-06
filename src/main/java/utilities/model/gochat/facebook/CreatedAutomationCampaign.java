package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CreatedAutomationCampaign {
	String createdBy;
	String createdDate;
	String lastModifiedBy;
	String lastModifiedDate;
	Integer id;
	String pageId;
	String campaignName;
	String timeType;
	String status;
	Integer storeChatId;
	Integer storeId;
	Object storeChat;
	Object storeConfig;
	Boolean isResponseInComment;
	String commentContent;
	Integer replies;
	Object[] campaignPosts;
	Object[] automatedCampaignKeyword;
	Object[] automatedCampaignComponent;
	Object component;
}