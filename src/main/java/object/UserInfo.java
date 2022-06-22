package object;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;


public class UserInfo {
    public static AppiumDriver<MobileElement> driver;
    public static String phoneNumber;
    public static String otp;
    public static String password;

    public static Long balance;

    public static AppiumDriver<MobileElement> getDriver() {
        return driver;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static String getOtp() {
        return otp;
    }

    public static String getPassword() {
        return password;
    }

    public static Long getBalance() {
        return balance;
    }
    public static void setBalance(Long balance) {
        UserInfo.balance = balance;
    }

    public static void setDriver(AppiumDriver<MobileElement> driver) {
        UserInfo.driver = driver;
    }

    public static void setUserInfo(String phoneNumber, String otp, String password) {
        UserInfo.phoneNumber = phoneNumber;
        UserInfo.otp = otp;
        UserInfo.password = password;
    }
}
