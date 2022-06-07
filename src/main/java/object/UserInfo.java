package object;

import io.appium.java_client.AppiumDriver;


public class UserInfo {
    public static AppiumDriver driver;

    public static void setDriver(AppiumDriver driver) {
        UserInfo.driver = driver;
    }

    public static String phoneNumber;
    public static String otp;
    public static String password;

    public UserInfo() {

    }
    public UserInfo(String phoneNumber, String otp, String password) {
        super();
        UserInfo.phoneNumber = phoneNumber;
        UserInfo.otp = otp;
        UserInfo.password = password;
    }
    public UserInfo(String phoneNumber, String password) {
        UserInfo.phoneNumber = phoneNumber;
        UserInfo.password = password;
    }

    public static void setPhoneNumber(String phoneNumber) {
        UserInfo.phoneNumber = phoneNumber;
    }
    public static void setOtp(String otp) {
        UserInfo.otp = otp;
    }
    public static void setPassword(String password) {
        UserInfo.password = password;
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
}
