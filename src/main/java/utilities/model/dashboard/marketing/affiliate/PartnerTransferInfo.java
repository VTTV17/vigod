package utilities.model.dashboard.marketing.affiliate;

import lombok.Data;

import java.util.List;

@Data
public class PartnerTransferInfo {
    String createdBy;
    String createdDate;
    String lastModifiedBy;
    String lastModifiedDate;
    int id;
    int originBranchId;
    int destinationBranchId;
    String status;
    String note;
    String storeId;
    List<Object> itemTransfers;
    int resellerStoreId;
    String handlingDataStatus;
    String transferType;
    List<Object> itemTransferLotDates;
    boolean hasLotLocation;
    boolean ignoreErrorWarning;
}
