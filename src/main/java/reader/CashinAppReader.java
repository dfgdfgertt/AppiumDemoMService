package reader;

import com.automation.test.reader.AbstractReader;
import helper.ElementHelper;
import object.UserInfo;
import org.testng.TestException;

import java.util.Objects;

public class CashinAppReader extends AbstractReader<String> {

    private String bankName = "";
    private final Long cashin;


    public CashinAppReader(Long cashin, String bankName) {
        this.cashin = cashin;
        this.bankName = bankName + "/Text";
    }

    public CashinAppReader(Long cashin) {
        this.cashin = cashin;
    }


    @Override
    public String read() throws Exception {
        ElementHelper helper = new ElementHelper(UserInfo.driver);
        try {
            String napTienIconId = "NẠP TIỀN/Text";
            helper.findElementByAccessibilityId(napTienIconId).click();
            String nhapSoTienInput = "Số tiền cần nạp/TextInput";
            helper.findElementByAccessibilityId(nhapSoTienInput).sendKeys(this.cashin.toString());
            if (!Objects.equals(this.bankName, "")) {
                helper.findElementByAccessibilityId(bankName).click();
            }
            String napTienBtnId = "Nạp tiền/Text";
            helper.findElementByAccessibilityId(napTienBtnId).click();
            String xacNhanBtnId = "Xác nhận/Text";
            helper.findElementByAccessibilityId(xacNhanBtnId).click();
            String nhapMatKhauId = "Nhập mật khẩu/Text";
            helper.findElementByAccessibilityIdIsDisplayed(nhapMatKhauId);
            helper.pressConfirmPassword(UserInfo.password);
            String thanhCongId = "Giao dịch thành công/Text";
            String result = helper.findElementByAccessibilityId(thanhCongId).getText();
            String homeId = "Màn hình chính/Text";
            helper.findElementByAccessibilityId(homeId).click();
            return result;
        } catch (TestException e) {
            throw new TestException("Nap tiền không thành công", e);
        }

    }
}