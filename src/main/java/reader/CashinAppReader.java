package reader;

import com.automation.test.reader.AbstractReader;
import helper.ElementHelper;
import object.UserInfo;
import org.testng.TestException;

public class CashinAppReader extends AbstractReader<String> {
    private String napTienIconId = "NẠP TIỀN/Text";
    private String napTienBtnId = "Nạp tiền/Text";
    private String xacNhanBtnId = "Xác nhận/Text";
    private String nhapSoTienInput = "Số tiền cần nạp/TextInput";
    private String nhapMatKhauId = "Nhập mật khẩu/Text";

    private String thanhCongId = "Giao dịch thành công/Text";
    private String homeId = "Màn hình chính/Text";

    private UserInfo info;
    private String bankName ="";
    private Long cashin;
    private ElementHelper helper;
    private boolean isErrorCase = false;

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    public void isErrorCase(boolean isErrorCase) {
        this.isErrorCase = isErrorCase;
    }


    public CashinAppReader(Long cashin, String bankName) {
        this.cashin = cashin;
        this.bankName = bankName+"/Text";
    }

    public CashinAppReader(Long cashin) {
        this.cashin = cashin;
    }


    @Override
    public String read() throws Exception {
        helper = new ElementHelper(info.driver);
        try {
            helper.findElementByAccessibilityId(napTienIconId).click();
            helper.findElementByAccessibilityId(nhapSoTienInput).sendKeys(this.cashin.toString());
            if (this.bankName != ""){
                helper.findElementByAccessibilityId(bankName).click();
            }
            helper.findElementByAccessibilityId(napTienBtnId).click();
            helper.findElementByAccessibilityId(xacNhanBtnId).click();
            helper.findElementByAccessibilityIdIsDisplayed(nhapMatKhauId);
            helper.pressConfirmPassword(info.password);
            String result = helper.findElementByAccessibilityId(thanhCongId).getText();
            helper.findElementByAccessibilityId(homeId).click();
            return result;
        }catch (TestException e){
            throw new TestException("Nap tiền không thành công",e);
        }

    }
}