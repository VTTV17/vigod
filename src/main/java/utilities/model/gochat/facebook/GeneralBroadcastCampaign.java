package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class GeneralBroadcastCampaign {
	String sendingStatus;
	Integer success;
	Integer totalSend;
	Integer id;
	String campaignName;
	String status;
}