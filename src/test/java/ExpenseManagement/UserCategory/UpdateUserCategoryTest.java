package ExpenseManagement.UserCategory;

import ExpenseManagement.AbstractExpenseManagementTest;
import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.UserInfo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class UpdateUserCategoryTest extends AbstractExpenseManagementTest {

    String categoryIdUserAddedOutHaveSub;
    String categoryIdUserAddedOutNoSub;
    String categoryIdUserAddedOutSub;
    String categoryIdOutSubDefault;
    String queryCountCategory = "select COUNT(*) from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s'";

    @BeforeClass
    public void setup() {
        String queryCategoryIdOutHaveSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s' AND ID IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%1$s' AND PARENT_ID IS NOT NULL)";
        String queryCategoryIdOutNoSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s' AND ID NOT IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%1$s' AND PARENT_ID IS NOT NULL)";
        String queryCategoryIdOutSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '2' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s'";
        String queryCategoryIdOutSubDefault =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '2' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s' AND PARENT_ID IN (SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP etr  where user_id = 'SYSTEM')";
        categoryIdUserAddedOutHaveSub = SQLHelper.executeQueryGetOneString(String.format(queryCategoryIdOutHaveSub, UserInfo.getPhoneNumber(), "OUT"));
        categoryIdUserAddedOutNoSub = SQLHelper.executeQueryGetOneString(String.format(queryCategoryIdOutNoSub, UserInfo.getPhoneNumber(), "OUT"));
        categoryIdUserAddedOutSub = SQLHelper.executeQueryGetOneString(String.format(queryCategoryIdOutSub, UserInfo.getPhoneNumber(), "OUT"));
        categoryIdOutSubDefault = SQLHelper.executeQueryGetOneString(String.format(queryCategoryIdOutSubDefault, UserInfo.getPhoneNumber(), "OUT"));
    }

    @DataProvider(name = "updateUserCategoryTestData")
    public Object[][] updateUserCategoryTestData() {
        return new Object[][]{
                {
                        "Case 6.1", "POST - Update user category - Type: OUT - Group: 1 - Have Subcategory", "/category/edit",
                        categoryIdUserAddedOutHaveSub, "61", "OUT"
                },
                {
                        "Case 6.2", "POST - Update user category - Type: OUT - Group: 1 - No Subcategory", "/category/edit",
                        categoryIdUserAddedOutNoSub, "62", "OUT"
                },
                {
                        "Case 6.3", "POST - Update user category - Type: OUT - Group: 2 - Subcategory of User Added", "/category/edit",
                        categoryIdUserAddedOutSub, "63", "OUT"
                },
                {
                        "Case 6.4", "POST - Update user category - Type: OUT - Group: 2 - Subcategory of Default Category", "/category/edit",
                        categoryIdUserAddedOutHaveSub, "64", "OUT"
                },

        };
    }

    @Test(dataProvider = "updateUserCategoryTestData")
    public void updateUserCategoryTest(String name, String description, String path, String categoryId, String iconId, String type) throws IOException {
        String queryGetDetail = "SELECT %s FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'";
        int count = SQLHelper.executeQueryCount(String.format(queryCountCategory, UserInfo.getPhoneNumber()));
        String parentId = SQLHelper.executeQueryGetOneString(String.format(queryGetDetail, "PARENT_ID" , UserInfo.getPhoneNumber(), categoryId));
        String levelGroup =  SQLHelper.executeQueryGetOneString(String.format(queryGetDetail, "LEVEL_GROUP" , UserInfo.getPhoneNumber(), categoryId));
        String requestBody = """
                {
                    "iconId": %s,
                    "name": "%s",
                    "id" : "%s"
                }""";
        String payload = String.format(requestBody, iconId, description, categoryId);
        String responseBody = """
                "time": 1656301783185,
                        "statusCode": 200,
                        "errorCode": 0,
                        "errorDes": null,
                        "icon": null,
                        "name": null,
                        "expenseCategories": null,
                        "category": {
                            "id": %s,
                            "groupName": "%s",
                            "categoryType": "%s",
                            "userId": "0909498114",
                            "iconId": %s,
                            "parentId": %s,
                            "levelGroup": %s,
                            "iconLink": null
                        },
                        "blacklist": null""";
        String expectedResponse = String.format(responseBody, categoryId, description, type, iconId, parentId, levelGroup);


        // create test case
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category before add category";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountCategory, UserInfo.getPhoneNumber()),count );

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedResponse), List.of("time"));

        String desc3 = "Verify the number of count user category after add category";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountCategory, UserInfo.getPhoneNumber()), count);

        String des4 = "Verify value of 'ICON_ID' field in SQL server is corrected";
        String query4 = String.format("SELECT ICON_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), categoryId);
        TestAction step4 = querySimpleData(des4, query4, iconId);

        String des5 = "Verify value of 'GROUP_NAME' field in SQL server is corrected";
        String query5 = String.format("SELECT GROUP_NAME FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), categoryId);
        TestAction step5 = querySimpleData(des5, query5, description);

        String des6 = "Verify value of 'CATEGORY_TYPE' field in SQL server is corrected";
        String query6 = String.format("SELECT CATEGORY_TYPE FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), categoryId);
        TestAction step6 = querySimpleData(des6, query6, type);

        String des7 = "Verify value of 'PARENT_ID' field in SQL server is corrected";
        String query7 = String.format("SELECT PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), categoryId);
        TestAction step7 = querySimpleData(des7, query7, parentId);

        String des8 = "Verify value of 'LEVEL_GROUP' field in SQL server is corrected";
        String query8 = String.format("SELECT LEVEL_GROUP FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), categoryId);
        TestAction step8 = querySimpleData(des8, query8, levelGroup);


        //add step & run
        tc.addStep(step1);
        tc.addStep(step2);
        tc.addStep(step3);
        tc.addStep(step4);
        tc.addStep(step5);
        tc.addStep(step6);
        tc.addStep(step7);
        tc.addStep(step8);
        tc.run();
    }
}
