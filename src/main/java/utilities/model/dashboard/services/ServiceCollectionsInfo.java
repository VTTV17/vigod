package utilities.model.dashboard.services;

import lombok.Data;

@Data
public class ServiceCollectionsInfo {
    private String collectionName;
    private String[] serviceList;
    private String SEOTitle;
    private String SEODescription;
    private String SEOKeywords;
    private String URLLink;
    private String collectionType;
    private boolean isSetPriorityForAll;
    private boolean setDuplicatePriority;
    private boolean inputPriority;
    private String[] automatedConditions;
    private boolean inputSEO;
    private String conditionType;
    private String collectionNameTranslation;
    private String SEOTitleTranslation;
    private String SEODescriptionTranslation;
    private String SEOKeywordTranslation;
    private String SEOUrlTranslation;
}
