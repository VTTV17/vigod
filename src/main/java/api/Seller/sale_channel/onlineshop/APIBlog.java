package api.Seller.sale_channel.onlineshop;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.onlineshop.ArticleInfo;
import utilities.model.dashboard.onlineshop.BlogCategoryInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIBlog {
    String GET_ARTICLE_PATH = "/beehiveservices/api/blog-articles?sort=lastModifiedDate,desc&storeId.equals=%s&deleted.equals=false&page=0&size=20";
    String GET_CATEGORY_PATH = "/beehiveservices/api/blog-categories?storeId=%s&page=0&size=9999";
    String CREATE_ARTICLE_PATH = "/beehiveservices/api/blog-articles";
    String CREATE_CATEGORY_PATH = "/beehiveservices/api/blog-categories";
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIBlog.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    public APIBlog(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public List<Integer> getArticleIdList(){
        Response response = api.get(GET_ARTICLE_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("content.id");
    }
    public Response callCreateArticle(){
        String random = new DataGenerator().generateString(10);
        String body = """
                {
                  "title": "%s",
                  "content": "<p>%s</p>",
                  "authorId": "",
                  "status": "PUBLISHED",
                  "categoryIds": [],
                  "storeId": "%s"
                }
                """.formatted("Article "+random, "Description "+random,loginInfo.getStoreID());
        Response response = api.post(CREATE_ARTICLE_PATH,loginInfo.getAccessToken(),body);
        return response;
    }
    public ArticleInfo createArticle(){
        Response response = callCreateArticle();
        response.then().statusCode(201);
        return response.as(ArticleInfo.class);
    }
    public int getArticleId(){
        List<Integer> listArticle = getArticleIdList();
        if(listArticle.size()==0){
            ArticleInfo articleInfo = createArticle();
            return articleInfo.getId();
        }else return listArticle.get(0);
    }
    public List<Integer> getBlogCategoryIdList(){
        Response response = api.get(GET_CATEGORY_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("content.id");
    }
    public Response callCreateBlogCategory(){
        String random = new DataGenerator().generateString(10);
        String body = """
                {
                  "title": "%s",
                  "description": "",
                  "seoTitle": "",
                  "seoDescription": "",
                  "seoKeywords": "",
                  "deleted": false,
                  "storeId": "%s"
                }
                """.formatted("Category "+random,loginInfo.getStoreID());
        return api.post(CREATE_CATEGORY_PATH,loginInfo.getAccessToken(),body);
    }
    public BlogCategoryInfo createBlogCategory(){
        Response response = callCreateBlogCategory();
        response.then().statusCode(201);
        return response.as(BlogCategoryInfo.class);
    }
    public BlogCategoryInfo createBlogCategory(BlogCategoryInfo blogCategory){
    	
		String payload="";
		try {
			payload = new ObjectMapper().writeValueAsString(blogCategory);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	
    	Response response = api.post(CREATE_CATEGORY_PATH,loginInfo.getAccessToken(), payload);
    	response.then().statusCode(201);
    	return response.as(BlogCategoryInfo.class);
    }
    public int getCategoryId(){
        List<Integer> categoryIdList = getBlogCategoryIdList();
        if(categoryIdList.size()==0){
            BlogCategoryInfo blogCategoryInfo = createBlogCategory();
            return blogCategoryInfo.getId();
        }else return categoryIdList.get(0);
    }
}
