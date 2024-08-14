package utilities.model.dashboard.catalog;

import java.util.List;

import lombok.Data;

@Data
public class District {
    public int id;
    public String code;
    public String inCountry;
    public String outCountry;
    public String zone;
    public int cityId;
    public List<Ward> wards;
}
