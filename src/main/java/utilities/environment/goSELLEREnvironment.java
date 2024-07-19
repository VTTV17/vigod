package utilities.environment;

import utilities.utils.PropertiesUtil;

public class goSELLEREnvironment {
    public final static String goSELLERAppName = PropertiesUtil.getEnvironmentData("goSELLERAppName");
    public final static String goSELLERBundleId = PropertiesUtil.getEnvironmentData("goSELLERBundleId");
    public final static String goSELLERLoginActivity = "com.mediastep.gosellseller.modules.credentials.login.LoginActivity";
    public final static String goSELLERHomeActivity = "com.mediastep.gosellseller.modules.tabs.main.MainActivity";
    public final static String goSELLERCreateProductActivity = "com.mediastep.gosellseller.modules.upload_product.CreateProductActivity";
    public final static String goSELLERProductManagementActivity = "com.mediastep.gosellseller.modules.product_management.ProductManagementActivity";
    public final static String goSELLERProductDetailActivity = "com.mediastep.gosellseller.modules.upload_product.CreateProductActivity";
    public final static String goSELLERProductBranchInventoryActivity = "com.mediastep.gosellseller.modules.upload_product.inventory";
   }
