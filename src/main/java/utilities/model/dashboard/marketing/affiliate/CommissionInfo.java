package utilities.model.dashboard.marketing.affiliate;

import lombok.Data;
import utilities.data.DataGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CommissionInfo {
    int id;
    String name = "Commission "+ new DataGenerator().generateString(10);
    int commissionType = 0;
    int type = 0;
    int rate = 10;
    int rateType = 0;
    int applyTo = 0;
    int applicableCondition = 0;
    Map<Integer,Integer> commissionApplicableLevels = Map.of(0,0);
    List<Map<String,String>> item = new ArrayList<>();
}
