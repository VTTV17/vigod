package utilities.model.staffPermission.OnlineStore;

import lombok.Data;

@Data
public class Blog{
	private boolean editBlogCategory;
	private boolean viewBlogCategoryList;
	private boolean translateCategory;
	private boolean addBlogCategory;
	private boolean editArticle;
	private boolean viewArticleDetail;
	private boolean translateArticle;
	private boolean addArticle;
	private boolean viewArticleList;
	private boolean deleteBlogCategory;
}