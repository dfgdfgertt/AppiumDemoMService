package helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URL;

public class AbstractMServiceInstall {

    AppiumDriver appiumDriver;
    ElementHelper elementHelper;
    final String onboardingId = "Ưu đãi thành viên\nThanh toán không giới hạn/Text";
    final String khamPhaNgayBtnId = "Khám phá ngay/Text";
    final String phoneNumberXpath = "//android.widget.EditText[@content-desc=\"Số điện thoại/TextInput\"]";
    final String tiepTucBtnId = "Tiếp tục/Text";
    final String goiChoToiBtnId = "Gọi cho tôi/Button";
    final String tiepTucBtnXpath = "(//android.view.ViewGroup[@content-desc=\"Tiếp tục/Button\"])[1]/android.view.ViewGroup";
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
        capabilities.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir") + app);

        appiumDriver = new AppiumDriver(new URL("http://localhost:4723/wd/hub"), capabilities);

        Thread.sleep(10000L);
    }

    @Parameters({"phoneNumber", "otp", "password"})
    @BeforeTest
    public void login(String phoneNumber, String otp, String password) throws Exception {
        elementHelper = new ElementHelper(appiumDriver);
        try {
            elementHelper.findElementByAccessibilityId(onboardingId).isDisplayed();
            elementHelper.findElementByAccessibilityId(khamPhaNgayBtnId).click();
            elementHelper.findElementByXpath(phoneNumberXpath).sendKeys(phoneNumber);
            elementHelper.findElementByAccessibilityId(tiepTucBtnId).click();
            if (elementHelper.findElementByAccessibilityIdIsDisplayed(goiChoToiBtnId, 2000)) {
                elementHelper.findElementByAccessibilityId(goiChoToiBtnId).click();
            }
            if (elementHelper.findElementByXPathIsDisplayed(tiepTucBtnXpath, 2000)) {
                elementHelper.pressKeys(otp, 2000L);
            }
            elementHelper.findElementByXpath(nhapMatKhauXpath, 10000).isDisplayed();
            elementHelper.pressKeys(password);
            String actual = elementHelper.findElementByAccessibilityId(napTienId).getText();
            Assert.assertEquals(actual, napTienText);
            System.out.println("Login success!\n===============================================\n\n");
        } catch (Exception e) {
            throw new Exception("Login failed!", e);
        }
    }


}
