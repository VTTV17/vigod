package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class TagManagement {
	String createdBy;
	String createdDate;
	String lastModifiedBy;
	String lastModifiedDate;
	int id;
	int storeId;
	String tagName;
	String tagColor;
	Boolean isShow;
}