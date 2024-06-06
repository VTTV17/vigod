package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class ConversationEntity {
	ConversationSender senders;
	String fbConversationId;
	String gsConversationId;
	String unread_count;
	String updated_time;
	@JsonIgnore
	String[] snippet;
}