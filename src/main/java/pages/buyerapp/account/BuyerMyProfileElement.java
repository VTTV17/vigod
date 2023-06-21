package pages.buyerapp.account;

import org.openqa.selenium.By;

public class BuyerMyProfileElement {
    By MY_PROFILE_HEADER_TITLE = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_general_edit_beecow_profile_toolbar_title')]");
    By MY_PROFILE_HEADER_SAVE_BTN = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_general_edit_beecow_profile_btn_save')]");
    By AVATAR = By.xpath("//android.widget.FrameLayout[contains(@resource-id,'fragment_general_edit_beecow_profile_avatar')]");
    By YOUR_NAME_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_fullname_notice')]/preceding-sibling::android.widget.TextView");
    By YOUR_NAME_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_yourname')]");
    By EMAIL_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'fragment_general_edit_beecow_profile_email_container')]//android.widget.TextView");
    By EMAIL_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_email')]");
    By IDENTITY_CARD_LBL = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_identity')]/parent::android.widget.RelativeLayout/preceding-sibling::android.widget.TextView");
    By IDENTITY_CARD_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_identity')]");
    By OTHER_EMAILS_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'flOtherEmailContainer')]/android.widget.RelativeLayout[1]/android.widget.TextView");
    By YOU_HAVE_OTHER_MAIL_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'tvOtherEmailCount')]");
    By PHONE_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'fragment_general_edit_beecow_profile_phone_container')]/preceding-sibling::android.widget.RelativeLayout/android.widget.TextView");
    By COUNTRY_NAME = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_country_name')]");
    By PHONE_CODE = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_country_phone_code')]");
    By PHONE_NUMBER_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_phone')]");
    By OTHER_PHONE_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'flOtherPhoneNumberContainer')]/android.widget.RelativeLayout[1]/android.widget.TextView");
    By YOUR_HAVE_OTHER_PHONE_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'tvOtherPhoneNumberCount')]");
    By COMPANY_NAME_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'edt_company_name')]");
    By COMPANY_NAME_LBL = By.xpath("//android.widget.EditText[contains(@resource-id,'edt_company_name')]/preceding-sibling::android.widget.TextView");
    By TAX_CODE_LBL = By.xpath("//android.widget.EditText[contains(@resource-id,'edt_tax_code')]/preceding-sibling::android.widget.TextView");
    By TAX_CODE_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'edt_tax_code')]");
    By PROFILE_ADDRESS_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'fragment_general_edit_beecow_profile_address_container')]/preceding-sibling::android.widget.RelativeLayout/android.widget.TextView");
    By ADDRESS_TXT = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_address')]");
    By GENDER_LBL = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'fragment_general_edit_beecow_profile_birthday_container')]/preceding-sibling::android.widget.TextView");
    By MAN_OPTION = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'fragment_general_edit_beecow_profile_st_user_gender')]/android.widget.TextView[1]");
    By WOMAN_OPTION = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'fragment_general_edit_beecow_profile_st_user_gender')]/android.widget.TextView[2]");
    By BIRTHDAY_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_birthday')]");
    By BIRTHDAY_LBL = By.xpath("//android.widget.EditText[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_et_birthday')]/parent::android.widget.RelativeLayout/preceding-sibling::android.widget.TextView");
    By CHANGE_PASSWORD_LBl = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_general_edit_beecow_profile_tv_change_password')]");
    By DELETE_ACCOUNT_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'tvDeleteAccount')]");
    By ADDRESS_HEADER_TITLE = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_edit_address_toolbar_title')]");
    By ADDRESS_HEADER_SAVE_BTN = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_edit_address_btn_save')]");
    By COUNTRY_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'activity_edit_address_ll_input_country')]/android.widget.TextView");
    By COUNTRY_DROPDOWN = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_country')]");
    By ADDRESS_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_edit_address_tv_address_text_count')]/preceding-sibling::android.widget.TextView");
    By ADDRESS_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_address')]");
    By CITY_PROVINCE_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'activity_edit_address_ll_input_city')]/android.widget.TextView");
    By CITY_PROVINCE_DROPDOWN = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_city')]");
    By DISTRICT_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'activity_edit_address_ll_input_district')]/android.widget.TextView");
    By DISTRICT_DROPDOWN = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_district')]");
    By WARD_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'activity_edit_address_ll_input_ward')]/android.widget.TextView");
    By WARD_DROPDOWN = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_ward')]");
    By ADDRESS_2_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_edit_address_tv_address2_text_count')]");
    By ADDRESS_2_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_address2')]");
    By STATE_REGION_PROVINCE_LBL = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'activity_edit_address_ll_input_state')]");
    By STAT_REGION_PROVINCE_DROPDOWN = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_state')]");
    By CITY_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_edit_address_tv_city_outside_vietnam_text_count')]/preceding-sibling::android.widget.TextView");
    By CITY_INPUT = By.xpath("//android.widget.EditText[contains(@resource-id,'activity_edit_address_et_city_outside_vietnam')]");
    By ZIP_CODE_LBL = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_edit_address_tv_zip_code_text_count')]");

}

