package utilities.model.gochat.facebook;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class ConversationSender {
	List<ConversationInfo> data;
}