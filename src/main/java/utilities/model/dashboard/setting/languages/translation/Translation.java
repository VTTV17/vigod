package utilities.model.dashboard.setting.languages.translation;

import java.util.List;

import lombok.Data;

/**
 * /ssrstorefront/api/custom-multi-language/langKey/zh-cn?defaultLangKey=en&storeId=267157
 */

@Data
public class Translation {
	List<StorefrontCSR> storefrontCSR;
	List<StorefrontSSR> storefrontSSR;
	List<MobileAndroid> mobileAndroid;
	List<MobileIOS> mobileIOS;
}