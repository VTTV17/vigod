package utilities.model.dashboard.onlineshop;

import lombok.Data;

import java.util.List;
@Data
public class ArticleInfo {
    String createdBy;
    String createdDate;
    String lastModifiedBy;
    String lastModifiedDate;
    int id;
    String title;
    String content;
    int authorId;
    String status;
    int storeId;
    boolean deleted;
    List<Integer> categories;
    String featuredTextOrContent;
}
