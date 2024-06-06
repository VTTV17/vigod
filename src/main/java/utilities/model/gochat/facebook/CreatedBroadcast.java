package utilities.model.gochat.facebook;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CreatedBroadcast {
	Integer id;
	Integer storeId;
	String campaignName;
	String pageId;
	String status;
	Integer totalSend;
	Integer totalSuccess;
	Integer storeChatId;
	Object storeChat;
	String sendingStatus;
	List<Object> lstBroadcastSegment;
	List<Object> lstBroadcastComponent;
	Object component;
}