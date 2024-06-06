package utilities.model.gochat.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class FBPost {
	String id;
	String postId;
	String message;
	String full_picture;
	Object attachments;
	String permalink_url;
	String created_time;
	String updated_time;
}