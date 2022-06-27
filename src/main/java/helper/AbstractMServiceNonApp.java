package helper;

import object.APIUrl;
import object.SQLConnectionInfo;
import object.UserInfo;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.sql.Connection;

public class AbstractMServiceNonApp {
    protected UserInfo info;
    protected SQLConnectionInfo sqlConnectionInfo;
    public static Connection connection;

    @Parameters({"phoneNumber", "otp", "password", "url", "dbUrl", "dbUsername", "dbPassword"})
    @BeforeSuite
    public void login(String phoneNumber, String otp, String password, String url, String dbUrl, String dbUsername, String dbPassword) throws Exception {
        UserInfo.setUserInfo(phoneNumber,otp,password);
        SQLConnectionInfo.setSQLConnectionInfo(dbUsername,dbPassword,dbUrl);
        connection = SQLHelper.CreateConnectionSQL(sqlConnectionInfo);
        UserInfo.setBalance(SQLHelper.getBalance(connection,phoneNumber));
        APIUrl.setURL(url);
    }
}
