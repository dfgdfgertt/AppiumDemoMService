package object;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;


public class UserInfo {
    public static AppiumDriver<MobileElement> driver;
    public static String phoneNumber;
    public static String otp;
    public static String password;


    public static void setDriver(AppiumDriver<MobileElement> driver) {
        UserInfo.driver = driver;
    }

    public static void setUserInfo(String phoneNumber, String otp, String password) {
        UserInfo.phoneNumber = phoneNumber;
        UserInfo.otp = otp;
        UserInfo.password = password;
    }
}
