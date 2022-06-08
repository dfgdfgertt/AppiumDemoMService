package helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import object.UserInfo;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URL;

public class AbstractMServiceApp{
    public AppiumDriver appiumDriver;
    public ElementHelper elementHelper;
    protected UserInfo info;
    final String nhapMatKhauXpath = "//android.view.ViewGroup[@content-desc=\"Nhập mật khẩu/Input/Typing\"]/android.view.ViewGroup/android.view.ViewGroup[2]";
    final String napTienId = "NẠP TIỀN/Text";
    final String napTienText = "NẠP TIỀN";


    @Parameters({"platformName", "platformVersion", "deviceName", "automationName", "app"})
    @BeforeSuite
    public void open(String platformName, String platformVersion, String deviceName, String automationName, String app) throws MalformedURLException, InterruptedException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, automationName);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
        capabilities.setCapability(MobileCapabilityType.APP,  app);
        capabilities.setCapability("noReset", "true");
        appiumDriver = new AppiumDriver(new URL("http://localhost:4723/wd/hub"), capabilities);
        Thread.sleep(10000L);
    }


    @Parameters({"phoneNumber", "otp", "password"})
    @BeforeTest
    public void login(String phoneNumber, String otp, String password) throws Exception {
        info = new UserInfo(phoneNumber,otp,password);
        info.setDriver(appiumDriver);
        elementHelper = new ElementHelper(appiumDriver);
        try {
            if (elementHelper.findElementByXPathIsDisplayed(nhapMatKhauXpath, 1000)) {
                elementHelper.pressKeys(password);
            }
            String actual = elementHelper.findElementByAccessibilityId(napTienId).getText();
            Assert.assertEquals(actual, napTienText);
            System.out.println("Login success!\n===============================================\n\n");
        } catch (Exception e) {
            throw new Exception("Login failed!", e);
        }
    }

}
