package utilities.model.dashboard.setting.languages;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class LanguageCatalog {
	int id;
	String countryCodeAlp2;
	String countryCodeAlp3;
	String countryName;
	String languageName;
	String displayValue;
	String langCode;
	String langCodeCombine;
	String orgLangCode;
	String langIcon;
}