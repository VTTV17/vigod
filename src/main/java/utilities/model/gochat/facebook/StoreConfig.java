package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class StoreConfig {
	Integer id;
	Integer storeId;
	String fbUserId;
	String fbName;
	String fbAvatar;
	Boolean isLogged;
}