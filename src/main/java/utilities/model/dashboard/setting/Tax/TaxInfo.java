package utilities.model.dashboard.setting.Tax;

import lombok.Data;

import java.util.List;

@Data
public class TaxInfo {
    private List<Integer> taxID;
    private List<Float> taxRate;
    private List<String> taxName;
}
