package ExpenseManagement.UserCategory;

import ExpenseManagement.AbstractExpenseManagementTest;
import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetUserCategoryTest extends AbstractExpenseManagementTest {
    @DataProvider(name = "getUserCategoryTestData")
    public Object[][] getUserCategoryTestData() {
        return new Object[][]{
                {
                        "Case 5", "GET - Get all user category", "/category"
                },
        };
    }

    @Test(dataProvider = "getUserCategoryTestData")
    public void getUserCategory(String name, String description, String path) throws IOException {
        String expectedCategory = """
                {
                                "id": %s,
                                "groupName": "%s",
                                "categoryType": "%s",
                                "userId": "%s",
                                "iconId": %s,
                                "parentId": %s,
                                "levelGroup": %s,
                                "iconLink": "%s"
                            }""";
        List<String> expectedResponse = new ArrayList<>(List.of(file.getFileContent("test-data/categoryDefault").split(",")));
        String query = """
                SELECT\s
                \temg.ID,
                \temg.GROUP_NAME,
                \temg.CATEGORY_TYPE,
                \temg.USER_ID,
                \temg.ICON_ID,
                \tCOALESCE(PARENT_ID,0) PARENT_ID,
                \temg.LEVEL_GROUP,
                \temi.ICON_LINK
                FROM\s
                \tSOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP emg
                \tJOIN\s
                \tSOAP_ADMIN.EXPENSE_MANAGEMENT_ICON emi
                \tON \s
                \temg.ICON_ID = emi.ID
                where\s
                \t(emg.user_id = '0909498114' OR emg.user_id ='SYSTEM')
                AND emg.DELETED IS NULL\s
                """;
        JSONArray listMoneySource = SQLHelper.executeQuery(query);
        for (int i = 0; i < listMoneySource.length(); i++) {
            JSONObject object = listMoneySource.getJSONObject(i);
            int id = object.getInt("ID");
            String groupName = object.getString("GROUP_NAME");
            String categoryType = object.getString("CATEGORY_TYPE");
            String userId = object.getString("USER_ID");
            int iconId = object.getInt("ICON_ID");
            int parentId = object.getInt("PARENT_ID");
            int levelGroup = object.getInt("LEVEL_GROUP");
            String iconLink = object.getString("ICON_LINK");
            String moneySource = String.format(expectedCategory, id, groupName, categoryType, userId, iconId, parentId, levelGroup, iconLink);
            expectedResponse.add(moneySource);
        }

        // create test case
        TestCase tc = new TestCase(name, description);

//        String desc1 = "Verify the number of count user category default";
//        String query1 = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = 'SYSTEM'";
//        TestAction step1 = executeCountQueryDb(desc1, query1, 84);


        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApiContains(desc2, path, signatureValue, null, HttpMethod.GET, expectedResponse, null);


        //add step & run
//        tc.addStep(step1);
        tc.addStep(step2);
        tc.run();
    }
}
