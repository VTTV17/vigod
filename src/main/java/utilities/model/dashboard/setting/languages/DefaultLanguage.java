package utilities.model.dashboard.setting.languages;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class DefaultLanguage {
	int id;
	int storeId;
	String langCode;
	Boolean isDefault;
	Boolean published;
	Boolean isInitial;
	String lastTranslateDate;
	String langIcon;
	String langName;
}
