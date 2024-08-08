package utilities.model.dashboard.onlineshop;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogCategoryInfo {
    String createdBy;
    String createdDate;
    String lastModifiedBy;
    String lastModifiedDate;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    int id;
    boolean deleted;
    String description;
    int storeId;
    String title;
    String seoDescription;
    String seoKeywords;
    String seoTitle;
}
