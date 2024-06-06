package utilities.model.gochat.facebook;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class AllConversation {
	List<ConversationEntity> data;
	String after;
	String afterConv;
	int totalUnreadConversation;
	boolean requiredResetPassword;
}