package utilities.model.dashboard.catalog;

import java.util.List;

import lombok.Data;

@Data
public class Ward{
    public int id;
    public String code;
    public String inCountry;
    public String outCountry;
    public int districtId;
}