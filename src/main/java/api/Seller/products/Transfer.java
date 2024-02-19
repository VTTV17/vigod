package api.Seller.products;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static api.Seller.products.APIAllProducts.SuggestionProductsInfo;

public class Transfer {
    String createTransferPath = "/itemservice/api/transfers/create";
    String getAllTransferPath = "/itemservice/api/transfers/store/%s?searchBy=id&transferType=BRANCH&page=%s&size=20&sort=id,desc";
    LoginDashboardInfo info;
    LoginInformation loginInformation;
    API api = new API();

    public Transfer(LoginInformation loginInformation) {
        info = new Login().getInfo(loginInformation);
        this.loginInformation = loginInformation;
    }

    @Data
    public static class TransferInfo {
        List<Integer> ids = new ArrayList<>();
        List<Integer> originBranchIds = new ArrayList<>();
        List<Integer> destinationBranchIds = new ArrayList<>();
    }

    String getCostList(String itemId, String modelId, int branchId, String code) {
        return """
                {
                	"itemId": "%s",
                	"modelId": "%s",
                	"branchId": %s,
                	"code": "%s",
                	"status": "AVAILABLE"
                }""".formatted(itemId, modelId, branchId, code);
    }

    String getItemTransfersWithoutVariationManageByProduct(int originBranchId) {
        SuggestionProductsInfo info = allProducts.getSuggestProductIdMatchWithConditions(originBranchId);

        String manageTypes = info.getInventoryManageTypes().get(0);
        String itemId = info.getItemIds().get(0);
        String modelId = info.getModelIds().get(0);
        String costList = manageTypes.equals("IMEI_SERIAL_NUMBER")
                ? getCostList(itemId, modelId, originBranchId, allProducts.getListIMEI(itemId, modelId, originBranchId).get(0))
                : "";
        return """
                {
                    "codeList": [%s],
                    "itemId": "%s",
                    "inventoryManageType": "%s",
                    "modelId": "%s",
                    "hasLot": false,
                    "hasLocation": false,
                    "locationDTOList": [],
                    "lotDateDTOList": [],
                    "quantity": 1
                }""".formatted(costList, itemId, manageTypes, modelId);

    }

    String getTransferBodyWithoutVariationManageByProduct(int destinationBranchId, int originBranchId) {
        return """
                {
                    "destinationBranchId": %s,
                    "originBranchId": %s,
                    "status": "READY_FOR_TRANSPORT",
                    "note": "",
                    "storeId": "%s",
                    "createdByStaffId": "%s",
                    "itemTransfers": [%s],
                    "hasLotLocation": false,
                    "ignoreErrorWarning": false
                }""".formatted(destinationBranchId, originBranchId, info.getStoreID(), info.getUserId(), getItemTransfersWithoutVariationManageByProduct(originBranchId));
    }

    APIAllProducts allProducts;

    int getOriginBranchId(List<Integer> assignedBranches) {
        return assignedBranches.stream()
                .filter(assignedBranch -> !allProducts.getSuggestProductIdMatchWithConditions(assignedBranch).getItemIds().isEmpty())
                .findFirst()
                .orElse(0);
    }

    public TransferInfo createAndGetTransferInfo() throws Exception {
        BranchManagement branchManagement = new BranchManagement(loginInformation);
        List<Integer> assignedBranches = info.getAssignedBranchesIds() != null ? info.getAssignedBranchesIds() : branchManagement.getInfo().getBranchID();
        List<Integer> destinationBranches = branchManagement.getDestinationBranchesInfo().getBranchID();

        allProducts = new APIAllProducts(loginInformation);
        int originBranchId = getOriginBranchId(assignedBranches);
        if (originBranchId != 0) {
            int destinationBranchId;
            if (destinationBranches.size() > 1) {
                // remove origin branch
                destinationBranches.remove((Integer) originBranchId);

                // select destination branch
                destinationBranchId = destinationBranches.get(0);
            } else throw new Exception("Must have at least 2 branches to create a new transfer.");
            int id = api.post(createTransferPath, info.getAccessToken(), getTransferBodyWithoutVariationManageByProduct(destinationBranchId, originBranchId))
                    .then()
                    .statusCode(201)
                    .extract()
                    .jsonPath()
                    .getInt("id");

            // init transfer information
            TransferInfo info = new TransferInfo();
            info.setIds(List.of(id));
            info.setOriginBranchIds(List.of(originBranchId));
            info.setDestinationBranchIds(List.of(destinationBranchId));

            // return info model
            return info;
        } else throw new Exception("No product that managed inventory by product and in-stock.");
    }

    Response getTransferResponse(int page) {
        return api.get(getAllTransferPath.formatted(info.getStoreID(), page), info.getAccessToken());
    }

    public TransferInfo getAllTransferInfo() {
        TransferInfo info = new TransferInfo();

        // get data page 0
        Response res = getTransferResponse(0);

        // else get all transfer information
        List<Integer> ids = new ArrayList<>();
        List<Integer> originBranchIds = new ArrayList<>();
        List<Integer> destinationBranchIds = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(res.getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 20;

        // get all inventory
        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
            JsonPath jPath = getTransferResponse(pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jPath.getList("id"));
            originBranchIds.addAll(jPath.getList("originBranchId"));
            destinationBranchIds.addAll(jPath.getList("destinationBranchId"));
        }
        // set transfer id
        info.setIds(ids);

        // set origin branch id
        info.setOriginBranchIds(originBranchIds);

        // set destination branch id
        info.setDestinationBranchIds(destinationBranchIds);

        return info;
    }

    public List<Integer> getTransferIdWithOriginAndDestinationBranchIsNotInListAssignedBranches(List<Integer> assignedBranches) {
        TransferInfo info = getAllTransferInfo();
        return IntStream.range(0, info.getIds().size()).filter(index -> !(assignedBranches.contains(info.getOriginBranchIds().get(index)) || assignedBranches.contains(info.getDestinationBranchIds().get(index)))).mapToObj(index -> info.getIds().get(index)).toList();
    }
}
