package helper;

import object.UserInfo;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

public class AbstractMServiceNonApp {

    @Parameters({"phoneNumber", "otp", "password"})
    @BeforeTest
    public void login(String phoneNumber, String otp, String password){
        UserInfo.setUserInfo(phoneNumber,otp,password);
    }
}
