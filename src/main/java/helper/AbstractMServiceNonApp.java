package helper;

import object.UserInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

public class AbstractMServiceNonApp {
    protected UserInfo info;

    @Parameters({"phoneNumber", "otp", "password"})
    @BeforeTest
    public void login(String phoneNumber, String otp, String password){
        info = new UserInfo(phoneNumber,otp,password);
    }
}
