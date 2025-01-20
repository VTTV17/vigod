package utilities.model.dashboard.setting.storeInformation;

import lombok.Data;

import java.util.List;

@Data
public class StoreInfo {
    private String storeURL;
    private String storeLogo;
    private String defaultLanguage;
    private List<String> storeLanguageList;
    private List<String> storeLanguageName;
    private List<String> sFLangList;
    private String timeZone;
    private String symbol; //Store's currency symbol
    private String dashboardDomain; //Store's domain - either VN or BIZ
}
