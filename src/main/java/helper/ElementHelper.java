package helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.WebElement;

public class ElementHelper {

    public AppiumDriver<MobileElement> driver;

    int timeOut = 180000;
    long sleep = 1000L;

    public ElementHelper(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
    }

    public boolean findElementByAccessibilityIdIsDisplayed(String id) throws Exception {
        for (int i = 0; i < (timeOut / 1000); i++) {
            if (!driver.findElementsByAccessibilityId(id).isEmpty()) {
                System.out.printf("Find '%s' success!%n", id);
                return true;
            }
            Thread.sleep(sleep);
        }
        return false;
    }




    public boolean findElementByAccessibilityIdIsDisplayed(String id, int timeOut) throws Exception {
        for (int i = 0; i < timeOut / 1000; i++) {
            if (!driver.findElementsByAccessibilityId(id).isEmpty()) {
                System.out.printf("Find '%s' success!%n", id);
                return true;
            }
            Thread.sleep(sleep);
            System.out.println("Wait: " + id + " time: " + i);
        }
        return false;
    }

    public boolean findElementByXPathIsDisplayed(String xpath) throws Exception {
        for (int i = 0; i < (timeOut / 1000); i++) {
            if (!driver.findElementsByXPath(xpath).isEmpty()) {
                System.out.printf("Find '%s' success!%n", xpath);
                return true;
            }
            Thread.sleep(sleep);
        }
        return false;
    }

    public boolean findElementByXPathIsDisplayed(String xpath, int timeOut) throws Exception {
        for (int i = 0; i < (timeOut / 1000); i++) {
            if (!driver.findElementsByXPath(xpath).isEmpty()) {
                System.out.printf("Find '%s' success!%n", xpath);
                return true;
            }
            Thread.sleep(sleep);
        }
        return false;
    }

    public WebElement findElementByAccessibilityId(String id) throws Exception {
        try {
            if (findElementByAccessibilityIdIsDisplayed(id)) {
                return driver.findElementByAccessibilityId(id);
            }
        } catch (Exception e) {
            throw new Exception(String.format("False to find '%s'!", id), e);
        }
        return null;

    }

    public WebElement findElementByAccessibilityId(String id, int timeOut) throws Exception {
        timeOut = timeOut / 1000;
        try {
            if (findElementByAccessibilityIdIsDisplayed(id, timeOut)) {
                return driver.findElementByAccessibilityId(id);
            }
        } catch (Exception e) {
            throw new Exception(String.format("False to find '%s'!", id), e);
        }
        return null;
    }

    public WebElement findElementByXpath(String xpath) throws Exception {
        try {
            if (findElementByXPathIsDisplayed(xpath)) {
                return driver.findElementByXPath(xpath);
            }
        } catch (Exception e) {
            throw new Exception(String.format("False to find '%s'!", xpath), e);
        }
        return null;
    }

    public WebElement findElementByXpath(String xpath, int timeOut) throws Exception {
        try {
            if (findElementByXPathIsDisplayed(xpath, timeOut)) {
                return driver.findElementByXPath(xpath);
            }
        } catch (Exception e) {
            throw new Exception(String.format("False to find '%s'!", xpath), e);
        }
        return null;
    }

    public void pressKeys(String keys) throws Exception {
        Thread.sleep(sleep);
        try {
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.substring(i, i + 1);
                driver.getKeyboard().sendKeys(key);
            }
            System.out.printf("Press '%s' success!%n", keys);
        } catch (Exception e) {
            throw new Exception(String.format("False type otp '%s'!", keys), e);
        }
    }

    public void pressKeys(String keys, long delayTimes) throws Exception {
        Thread.sleep(delayTimes);
        try {
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.substring(i, i + 1);
                driver.getKeyboard().sendKeys(key);
            }
            System.out.printf("Press '%s' success!%n", keys);
        } catch (Exception e) {
            throw new Exception(String.format("False type otp '%s'!", keys), e);
        }
    }

    public void pressConfirmPassword(String keys) throws Exception {
        Thread.sleep(sleep);
        try {
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.substring(i, i + 1);
                switch (key){
                    case "1": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[1]").click();
                    case "2": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[2]").click();
                    case "3": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[3]").click();
                    case "4": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[4]").click();
                    case "5": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[5]").click();
                    case "6": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[6]").click();
                    case "7": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[7]").click();
                    case "8": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[9]").click();
                    case "9": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[11]").click();
                    case "0": driver.findElementByXPath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup[2]/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[10]").click();
                }
            }
            System.out.printf("Press '%s' success!%n", keys);
        } catch (Exception e) {
            throw new Exception(String.format("False type password '%s'!", keys), e);
        }
    }

}
