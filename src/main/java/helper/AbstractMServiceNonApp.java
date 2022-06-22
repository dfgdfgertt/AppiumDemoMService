package helper;

import object.APIUrl;
import object.SQLConnectionInfor;
import object.UserInfo;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.sql.Connection;

public class AbstractMServiceNonApp {
    protected UserInfo info;

    @Parameters({"phoneNumber", "otp", "password", "url", "dbUrl", "dbUsername", "dbPassword"})
    @BeforeTest
    public void login(String phoneNumber, String otp, String password, String url, String dbUrl, String dbUsername, String dbPassword) throws Exception {
        UserInfo.setUserInfo(phoneNumber,otp,password);
        SQLConnectionInfor sqlConnectionInfor = new SQLConnectionInfor(dbUsername,dbPassword,dbUrl);
        Connection connection = SQLHelper.CreateConnectionSQL(sqlConnectionInfor);
        UserInfo.setBalance(SQLHelper.getBalance(connection,phoneNumber));
        APIUrl.setURL(url);
    }
}
