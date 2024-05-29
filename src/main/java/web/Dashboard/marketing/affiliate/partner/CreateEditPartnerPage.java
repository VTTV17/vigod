package web.Dashboard.marketing.affiliate.partner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;

public class CreateEditPartnerPage extends CreateEditPartnerElement{
    final static Logger logger = LogManager.getLogger(CreateEditPartnerPage.class);
    UICommonAction common;
    WebDriver driver;
    public CreateEditPartnerPage(WebDriver driver){
        this.driver = driver;
        common = new UICommonAction(driver);
    }

    public String inputName(String...name){
        String partnerName;
        if(name.length==0){
            partnerName ="partner" + new DataGenerator().randomNumberGeneratedFromEpochTime(10);
        }else partnerName = name[0];
        common.inputText(loc_txtName,partnerName);
        logger.info("Input name: "+partnerName);
        return partnerName;
    }
    public String inputEmail(String...email){
        String partnerEmail;
        if(email.length==0){
            partnerEmail = "email" + new DataGenerator().randomNumberGeneratedFromEpochTime(5)+"@mailnesia.com";
        }else partnerEmail = email[0];
        common.inputText(loc_txtEmail,partnerEmail);
        logger.info("Input partner email: "+partnerEmail);
        return partnerEmail;
    }
    public String inputPhoneNumber(String...phone){
        String phoneNumber;
        if(phone.length==0){
            phoneNumber = "01"+ new DataGenerator().randomNumberGeneratedFromEpochTime(10);
        }else phoneNumber = phone[0];
        common.inputText(loc_txtPhoneNumber,phoneNumber);
        logger.info("Input phone number: "+phoneNumber);
        return phoneNumber;
    }
    public void unselectReseller(){
        common.uncheckTheCheckboxOrRadio(loc_chkReseller_value,loc_chkReseller_action);
        logger.info("Unselect Reseller.");
    }
    public void clickOnSaveBtn(){
        common.click(loc_btnSave);
        logger.info("Click on Save button.");
    }
    public void createSimpleDropshipVN(boolean saveOrNotFlag){
        unselectReseller();
        inputName();
        inputEmail();
        inputPhoneNumber();
        if(saveOrNotFlag) clickOnSaveBtn();
    }
    public void inputAddress(String...addresss){
        String addressValue;
        if(addresss.length ==0){
           addressValue ="address " + new DataGenerator().generateString(10);
        }else addressValue = addresss[0];
        common.inputText(loc_txtAddress,addressValue);
        logger.info("Input address: "+addressValue);
    }
    public void selectCityProvince(String...cityProvince){
        String cityProvinceValue;
        if (cityProvince.length == 0){
            common.waitTillSelectDropdownHasData(loc_ddlCityProvince);
            common.selectByIndex(loc_ddlCityProvince, new DataGenerator().generatNumberInBound(1,common.getAllOptionInDropDown(common.getElement(loc_ddlCityProvince)).size()));
            cityProvinceValue =  common.getDropDownSelectedValue(loc_ddlCityProvince);
        }else {
            common.selectByVisibleText(loc_ddlCityProvince, cityProvince[0]);
            cityProvinceValue = cityProvince[0];
        }
        logger.info("Select city/province: "+cityProvinceValue);
    }
    public void selectDistrict(String...district){
        String districtValue;
        if (district.length == 0){
            common.waitTillSelectDropdownHasData(loc_ddlDistrict);
            common.selectByIndex(loc_ddlDistrict, new DataGenerator().generatNumberInBound(1,common.getAllOptionInDropDown(common.getElement(loc_ddlDistrict)).size()));
            districtValue =  common.getDropDownSelectedValue(loc_ddlDistrict);
        }else {
            common.selectByVisibleText(loc_ddlDistrict, district[0]);
            districtValue = district[0];
        }
        logger.info("Select district: "+districtValue);
    }
    public void selectWard(String...ward){
        String wardValue;
        if (ward.length == 0){
            common.waitTillSelectDropdownHasData(loc_ddlWard);
            common.selectByIndex(loc_ddlWard, new DataGenerator().generatNumberInBound(1,common.getAllOptionInDropDown(common.getElement(loc_ddlWard)).size()));
            wardValue =  common.getDropDownSelectedValue(loc_ddlWard);
        }else {
            common.selectByVisibleText(loc_ddlWard, ward[0]);
            wardValue = ward[0];
        }
        logger.info("Select ward: "+wardValue);
    }
    public void unselectDropship(){
        common.uncheckTheCheckboxOrRadio(loc_chkDropship_value, loc_chkDropship_action);
        logger.info("Unselect Dropship.");
    }
    public void createSimpleResellerVN(boolean saveOrNotFlag){
        unselectDropship();
        inputName();
        inputEmail();
        inputPhoneNumber();
        inputAddress();
        selectCityProvince();
        selectDistrict();
        selectWard();
        if(saveOrNotFlag) clickOnSaveBtn();
    }
}
