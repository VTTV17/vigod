package utilities.model.dashboard.onlineshop;

import lombok.Data;

@Data
public class BlogCategoryInfo {
    String createdBy;
    String createdDate;
    String lastModifiedBy;
    String lastModifiedDate;
    int id;
    boolean deleted;
    String description;
    int storeId;
    String title;
    String seoDescription;
    String seoKeywords;
    String seoTitle;
}
