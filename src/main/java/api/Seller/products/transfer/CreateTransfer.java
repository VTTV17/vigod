package api.Seller.products.transfer;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.setting.BranchManagement;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.stream.IntStream;

import static api.Seller.products.all_products.APIAllProducts.*;
import static org.apache.commons.lang.math.JVMRandom.nextLong;

public class CreateTransfer {
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();

    public CreateTransfer(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getCost(String itemId, String modelId, int branchId, String code) {
        return """
                {
                	"itemId": "%s",
                	"modelId": "%s",
                	"branchId": %s,
                	"code": "%s",
                	"status": "AVAILABLE"
                }""".formatted(itemId, modelId, branchId, code);
    }

    List<String> getCostList(String itemId, String modelId, int branchId, List<String> imeiList, long transferredQuantity) {
        return IntStream.iterate(0, index -> index < transferredQuantity, index -> index + 1)
                .mapToObj(index -> getCost(itemId, modelId, branchId, imeiList.get(index)))
                .toList();
    }

    String getItemTransfers(int originBranchId) {
        SuggestionProductsInfo info = allProducts.getSuggestProductIdMatchWithConditions(originBranchId);

        String manageTypes = info.getInventoryManageTypes().get(0);
        String itemId = info.getItemIds().get(0);
        String modelId = info.getModelIds().get(0);
        long transferredQuantity = nextLong(info.getRemainingStocks().get(0)) + 1;
        String costList = manageTypes.equals("IMEI_SERIAL_NUMBER")
                ? getCostList(itemId, modelId, originBranchId, allProducts.getListIMEI(itemId, modelId, originBranchId), transferredQuantity).toString()
                : "[]";
        return """
                {
                    "codeList": %s,
                    "itemId": "%s",
                    "inventoryManageType": "%s",
                    "modelId": "%s",
                    "hasLot": false,
                    "hasLocation": false,
                    "locationDTOList": [],
                    "lotDateDTOList": [],
                    "quantity": %s
                }""".formatted(costList, itemId, manageTypes, modelId, transferredQuantity);

    }

    String getTransferBody(int destinationBranchId, int originBranchId) {
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
                }""".formatted(destinationBranchId, originBranchId, loginInfo.getStoreID(), loginInfo.getUserId(), getItemTransfers(originBranchId));
    }

    int getOriginBranchId(List<Integer> assignedBranches) {
        return assignedBranches.stream()
                .filter(assignedBranch -> !allProducts.getSuggestProductIdMatchWithConditions(assignedBranch).getItemIds().isEmpty())
                .findFirst()
                .orElse(0);
    }

    APIAllProducts allProducts;
    String createTransferPath = "/itemservice/api/transfers/create";
    public TransferManagement.TransferInfo createAndGetTransferInfo() throws Exception {
        // init branch management API
        BranchManagement branchManagement = new BranchManagement(loginInformation);
        // get assigned branches
        List<Integer> assignedBranchIds = (loginInfo.getAssignedBranchesIds() != null)
                ? loginInfo.getAssignedBranchesIds() // staff
                : branchManagement.getInfo().getBranchID(); // seller
        // get destination branches
        List<Integer> destinationBranches = branchManagement.getDestinationBranchesInfo().getBranchID();

        // init get all products API
        allProducts = new APIAllProducts(loginInformation);

        // find origin branch that have in-stock product
        int originBranchId = getOriginBranchId(assignedBranchIds);

        // create transfer
        if (originBranchId != 0) {
            int destinationBranchId;
            if (destinationBranches.size() > 1) {
                // remove origin branch
                destinationBranches.remove((Integer) originBranchId);

                // select destination branch
                destinationBranchId = destinationBranches.get(0);
            } else throw new Exception("Must have at least 2 branches to create a new transfer.");

            // create and get transferId
            int transferId = api.post(createTransferPath, loginInfo.getAccessToken(), getTransferBody(destinationBranchId, originBranchId))
                    .then()
                    .statusCode(201)
                    .extract()
                    .jsonPath()
                    .getInt("id");

            // init transfer information
            TransferManagement.TransferInfo info = new TransferManagement.TransferInfo();
            info.setIds(List.of(transferId));
            info.setOriginBranchIds(List.of(originBranchId));
            info.setDestinationBranchIds(List.of(destinationBranchId));

            // return info model
            return info;
        } else throw new Exception("No product that managed inventory by product and in-stock.");
    }
}
