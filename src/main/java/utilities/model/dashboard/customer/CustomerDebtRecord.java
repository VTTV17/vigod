package utilities.model.dashboard.customer;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import utilities.enums.DebtActionEnum;

/**
 * Used for deserialization of JSON into POJOs 
 * Example base path: /orderservices2/api/customer-debt/get-all/storeId/203833/customerId/4983852?page=0&size=50
 */

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerDebtRecord {
    public Integer id;
    public Integer customerId;
    public DebtActionEnum action;
    public BigDecimal amount;
    public BigDecimal debt;
    public String refId;
    public String refIdType;
    public String createdDate;
}