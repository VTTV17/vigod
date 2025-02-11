package utilities.utils.localization;

import java.util.List;

import utilities.model.dashboard.setting.languages.translation.ITranslation;

public class TranslateText {

	public static String localizedText(List<? extends ITranslation> translation, String key) {
		return translation.stream()
				.filter(e -> e.getKey().contentEquals(key))
				.findFirst()
				.map(e -> e.getValue())
				.orElse("");
	}
}
