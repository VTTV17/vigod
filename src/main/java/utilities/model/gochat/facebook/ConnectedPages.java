package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class ConnectedPages {
	int id;
	int storeId;
	String fbUserId;
	String usingStatus;
	String pageId;
	String pageToken;
	String pageName;
	String pageUrl;
	String avatar;
}