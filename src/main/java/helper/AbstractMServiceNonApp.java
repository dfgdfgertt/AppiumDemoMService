package helper;

import object.APIUrl;
import object.SQLConnectionInfo;
import object.UserInfo;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;


public class AbstractMServiceNonApp {

    @Parameters({"phoneNumber", "otp", "password", "url", "dbUrl", "dbUsername", "dbPassword"})
    @BeforeSuite
    public void login(String phoneNumber, String otp, String password, String url, String dbUrl, String dbUsername, String dbPassword) throws Exception {
        UserInfo.setUserInfo(phoneNumber,otp,password);
        SQLConnectionInfo.setSQLConnectionInfo(dbUsername,dbPassword,dbUrl);
        UserInfo.setBalance(SQLHelper.getBalance(phoneNumber));
        APIUrl.setURL(url);
    }
}
