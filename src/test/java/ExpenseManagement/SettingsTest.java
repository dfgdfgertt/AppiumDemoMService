package ExpenseManagement;

import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;


public class SettingsTest extends AbstractExpenseManagementTest {

    private final String expectedValue = "10";
    private final String expectedActive = "1";
    private final String setting = String.format("""
            {
                            "settingType": "REMIND_NOTI",
                            "value": "%s",
                            "userId": "0909498114",
                            "active": %s
                        }""", expectedValue, expectedActive);

    @DataProvider(name = "settingsTestData")
    public Object[][] settingsTestData() {
        return new Object[][]{
                {
                        "Case 2", "POST - Edit setting", "/setting/edit",
                        """
{
  "setting": {
    "settingType": "REMIND_NOTI",
    "value": "%s",
    "active": %s
  },
  "msgType": "UPDATE_SETTING"
}""",
                        """
"statusCode": 200,
        "errorCode": 0,
        "errorDes": null,
        "setting": %s"""
                }
        };
    }

    @Test(dataProvider = "settingsTestData")
    public void editSettings(String name, String desc, String path, String payload, String expectedBody) throws IOException {
        // create test case
        TestCase tc = new TestCase(name, desc);

        // create test step 1
        String des1 = "Verify response data of request";
        TestAction step1 = sendApiContains(des1, path, signatureValue, String.format(payload,expectedValue,expectedActive), HttpMethod.POST, String.format(expectedBody, setting), null);

        String des2 = "Verify value of 'Value' field in SQL server is corrected";
        String query2 = "SELECT VALUE FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_SETTINGS where user_id = '0909498114'";
        TestAction step2 = querySimpleData(des2, query2, expectedValue);

        String des3 = "Verify 'Active' field in SQL server is corrected";
        String query3 = "SELECT ACTIVE FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_SETTINGS where user_id = '0909498114'";
        TestAction step3 = querySimpleData(des3, query3, expectedActive);

        tc.addStep(step1);
        tc.addStep(step2);
        tc.addStep(step3);
        tc.run();
    }

    @DataProvider(name = "getSettingsTestData")
    public Object[][] getSettingsTestData() {
        return new Object[][]{
                {
                        "Case 3", "GET - Get settings", "/setting",
                        """
"statusCode": 200,
        "errorCode": 0,
        "errorDes": null,
        "expenseSettings": [
            %s
        ]""",
                        1
                }
        };
    }

    @Test(dataProvider = "getSettingsTestData")
    public void getSettings(String name, String description, String path, String expectedBody, int expectedCountNumber) throws IOException {
        // create test case
        TestCase tc = new TestCase(name, description);

        String desc = "Verify response data of default settings (New User)";
        String signatureNewUser = file.getFileContent("test-data/signatureNewUser");
        String tokenNewUser = file.getFileContent("test-data/tokenNewUser");
        TestAction step = sendApiContains(desc, path, signatureNewUser , tokenNewUser, null, HttpMethod.GET, String.format(expectedBody,""), null);

        // create test step 1
        String desc1 = "Verify response data of request";
        TestAction step1 = sendApiContains(desc1, path, signatureValue, null, HttpMethod.GET, String.format(expectedBody, setting), null);

        String desc2 = "Verify number of count setting config is corrected";
        String query2 = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_SETTINGS where user_id = '0909498114'";
        TestAction step2 = executeCountQueryDb(desc2, query2, expectedCountNumber);


        //add step & run
        tc.addStep(step);
        tc.addStep(step1);
        tc.addStep(step2);
        tc.run();
    }
}

