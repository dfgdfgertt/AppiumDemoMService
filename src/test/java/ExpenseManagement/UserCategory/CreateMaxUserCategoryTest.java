package ExpenseManagement.UserCategory;

import ExpenseManagement.AbstractExpenseManagementTest;
import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.UserInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class CreateMaxUserCategoryTest extends AbstractExpenseManagementTest {

    private int maxCategoryIN = 0;
    private int maxCategoryOUT = 0;
    private final String queryCountCategory = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE  user_id = '%s' AND CATEGORY_TYPE = '%s' AND DELETED IS NULL";


    @BeforeMethod
    public void createMaxUserCategory() {
        maxCategoryIN += SQLHelper.executeQueryCount(String.format(queryCountCategory, UserInfo.getPhoneNumber(), "IN"));
        maxCategoryOUT += SQLHelper.executeQueryCount(String.format(queryCountCategory, UserInfo.getPhoneNumber(), "OUT"));
        for (int i = maxCategoryIN; i < 20; i++) {
            if (addCategory(i + 1, "IN")) {
                System.out.println("add success number:" + i);
            }
        }
        for (int i = maxCategoryOUT; i < 10; i++) {
            if (addCategory(i + 1, "OUT")) {
                System.out.println("add success number:" + i);
            }
        }
    }

    @DataProvider(name = "addUserCategoryTestData")
    public Object[][] addUserCategoryTestData() {
        return new Object[][]{
                {
                        "Case 4.11", "POST - Add user category - Failed when User was added to max 20 Type IN", "/category",
                        "98", "IN", "0", 20
                },
                {
                        "Case 4.12", "POST - Add user category - Failed when User was added to max 10 Type OUT", "/category",
                        "98", "OUT", "0", 10
                },
        };
    }

    @Test(dataProvider = "addUserCategoryTestData")
    public void addUserCategoryTest(String name, String description, String path, String iconId, String type, String parentId, int max) throws IOException {
        String requestBody = """
                {
                    "iconId": %s,
                    "name": "%s",
                    "type" : "%s",
                    "parentId" : "%s"
                }""";
        String payload = String.format(requestBody, iconId, description, type, parentId);
        String responseBody = """
                {
                    "user": "%s",
                    "result": false,
                    "errorCode": -90,
                    "errorDesc": "",
                    "data": {
                      "time": 1656902098805,
                      "statusCode": 200,
                      "errorCode": -90,
                      "errorDes": "Lỗi hệ thống",
                      "icon": null,
                      "name": null,
                      "expenseCategories": null,
                      "category": null,
                      "blacklist": null
                    }
                  }""";


        // create test case
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category before add category";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountCategory, UserInfo.getPhoneNumber(), type), max);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseBody, UserInfo.getPhoneNumber()), List.of("time"));

        String desc3 = "Verify the number of count user category after add category";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountCategory, UserInfo.getPhoneNumber(), type), max);


        //add step & run
        tc.addStep(step1);
        tc.addStep(step2);
        tc.addStep(step3);
        tc.run();
    }

}
