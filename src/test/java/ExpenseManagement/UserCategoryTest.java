package ExpenseManagement;

import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.UserInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserCategoryTest extends AbstractExpenseManagementTest {

    private List<Integer> listEdit = new ArrayList<>();

    private int id = 300;
    private int idInNoSub = 0;
    private int idINSub = 0;
    private int idOutNoSub = 0;
    private int idOutSub = 0;
    String queryCountCategory = "select COUNT(*) from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s'";

    @BeforeClass
    public void setupCategory() throws SQLException {
        String queryIdInNoSub =
                "SELECT MIN(ID) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = 'SYSTEM' AND CATEGORY_TYPE = 'IN' AND ID NOT IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = 'SYSTEM' AND PARENT_ID IS NOT NULL)";
        String queryidINSub =
                "SELECT MIN(ID) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = 'SYSTEM' AND CATEGORY_TYPE = 'IN' AND ID IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = 'SYSTEM' AND PARENT_ID IS NOT NULL)";
        String queryidOutNoSub =
                "SELECT MIN(ID) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = 'SYSTEM' AND CATEGORY_TYPE = 'OUT' AND ID NOT IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = 'SYSTEM' AND PARENT_ID IS NOT NULL)";
        String queryidOutSub =
                "SELECT MIN(ID) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = 'SYSTEM' AND CATEGORY_TYPE = 'OUT' AND ID IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = 'SYSTEM' AND PARENT_ID IS NOT NULL)";
        id += SQLHelper.executeQueryCount(connection, String.format(queryCountCategory, UserInfo.getPhoneNumber()));
        idInNoSub = SQLHelper.executeQueryCount(connection, queryIdInNoSub);
        idINSub = SQLHelper.executeQueryCount(connection, queryidINSub);
        idOutNoSub = SQLHelper.executeQueryCount(connection, queryidOutNoSub);
        idOutSub = SQLHelper.executeQueryCount(connection, queryidOutSub);
    }

    @DataProvider(name = "addUserCategoryTestData")
    public Object[][] addUserCategoryTestData() {
        return new Object[][]{
                {
                        "Case 4.1", "POST - Add user category - Type: IN - Group: 1", "/category",
                        "98", "IN", "0", "1"
                },
                {
                        "Case 4.2", "POST - Add user category - Type: IN - Group: 2 - Parent: User Created", "/category",
                        "97", "IN", String.valueOf(id + 1), "2"

                },
                {
                        "Case 4.3", "POST - Add user category - Type: IN - Group: 2 - Parent: Default No Subcategory", "/category",
                        "102", "IN", String.valueOf(idInNoSub), "2"

                },
                {
                        "Case 4.4", "POST - Add user category - Type: IN - Group: 2 - Parent: Default Have Subcategory", "/category",
                        "101", "IN", String.valueOf(idINSub), "2"
                },
                {
                        "Case 4.5", "POST - Add user category - Type: OUT - Group: 1", "/category",
                        "103", "OUT", "0", "1"
                },
                {
                        "Case 4.6", "POST - Add user category - Type: OUT - Group: 2 - Parent: User Created", "/category",
                        "103", "OUT", String.valueOf(id + 1), "2"
                },
                {
                        "Case 4.7", "POST - Add user category - Type: OUT - Group: 2 - Parent: Default No Subcategory", "/category",
                        "103", "OUT", String.valueOf(idOutNoSub), "2"
                },
                {
                        "Case 4.8", "POST - Add user category - Type: OUT - Group: 2 - Parent: Default Have Subcategory", "/category",
                        "103", "OUT", String.valueOf(idOutSub), "2"
                },
        };
    }

    @Test(dataProvider = "addUserCategoryTestData")
    public void addUserCategoryTest(String name, String description, String path, String iconId, String type, String parentId, String levelGroup) throws IOException {
        id++;
        listEdit.add(id);
        String requestBody = """
                {
                    "iconId": %s,
                    "name": "%s",
                    "type" : "%s",
                    "parentId" : "%s"
                }""";
        String payload = String.format(requestBody, iconId, description, type, parentId);
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
        String expectedResponse = String.format(responseBody, id, description, type, iconId, parentId, levelGroup);


        // create test case
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category before add category";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountCategory, UserInfo.getPhoneNumber()), id - 301);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedResponse), List.of("time"));

        String desc3 = "Verify the number of count user category after add category";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountCategory, UserInfo.getPhoneNumber()), id - 300);


        String des9 = "Verify value of 'ID' field in SQL server is corrected";
        String query9 = String.format("SELECT MAX(ID) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s'", UserInfo.getPhoneNumber());
        TestAction step9 = querySimpleData(des9, query9, String.valueOf(id));

        String des4 = "Verify value of 'ICON_ID' field in SQL server is corrected";
        String query4 = String.format("SELECT ICON_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step4 = querySimpleData(des4, query4, iconId);

        String des5 = "Verify value of 'GROUP_NAME' field in SQL server is corrected";
        String query5 = String.format("SELECT GROUP_NAME FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step5 = querySimpleData(des5, query5, description);

        String des6 = "Verify value of 'CATEGORY_TYPE' field in SQL server is corrected";
        String query6 = String.format("SELECT CATEGORY_TYPE FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step6 = querySimpleData(des6, query6, type);

        String des7 = "Verify value of 'PARENT_ID' field in SQL server is corrected";
        String query7 = String.format("SELECT PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step7 = querySimpleData(des7, query7, parentId);

        String des8 = "Verify value of 'LEVEL_GROUP' field in SQL server is corrected";
        String query8 = String.format("SELECT LEVEL_GROUP FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step8 = querySimpleData(des8, query8, levelGroup);


        //add step & run
        tc.addStep(step1);
        tc.addStep(step2);
        tc.addStep(step3);
        tc.addStep(step9); // kiểm tra id
        tc.addStep(step4);
        tc.addStep(step5);
        tc.addStep(step6);
        tc.addStep(step7);
        tc.addStep(step8);
        tc.run();
    }

    @DataProvider(name = "getUserCategoryTestData")
    public Object[][] getUserCategoryTestData() {
        return new Object[][]{
                {
                        "Case 5", "GET - Get all user category", "/category"
                },
        };
    }

    @Test(dataProvider = "getUserCategoryTestData")
    public void getUserCategory(String name, String description, String path) throws IOException, SQLException {
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
                \temg.user_id = '0909498114' OR emg.user_id ='SYSTEM'""";
        JSONArray listMoneySource = SQLHelper.executeQuery(connection, query);
        for (int i = 0; i < (listMoneySource != null ? listMoneySource.length() : 0); i++) {
            JSONObject object = listMoneySource.getJSONObject(i);
            int id = object.getInt("ID");
            String groupName = object.getString("GROUP_NAME");
            String categoryType = object.getString("CATEGORY_TYPE");
            String userId = object.getString("USER_ID");
            int iconId = object.getInt("ICON_ID");
            int parentId = object.getInt("PARENT_ID");
            int levelGroup = object.getInt("LEVEL_GROUP");
            String iconLink = object.getString("ICON_LINK");
            String moneySource = String.format(expectedCategory,id,groupName,categoryType,userId,iconId,parentId,levelGroup,iconLink);
            expectedResponse.add(moneySource);
        }

        // create test case
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category default";
        String query1 = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = 'SYSTEM'";
        TestAction step1 = executeCountQueryDb(desc1, query1, 84);


        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApiContains(desc2, path, signatureValue, null, HttpMethod.GET, expectedResponse, null);


        //add step & run
//        tc.addStep(step1);
        tc.addStep(step2);
        tc.run();
    }

    @DataProvider(name = "editUserCategoryTestData")
    public Object[][] editUserCategoryTestData() {
        return new Object[][]{
                {
                        "Case 6.1", "POST - Add user category - Type: IN - Group: 1", "/category",
                        "98", "IN", "0", "1"
                },
                {
                        "Case 6.2", "POST - Add user category - Type: IN - Group: 2 - Parent: Was Created", "/category",
                        "97", "IN", String.valueOf(id + 1), "2"

                },
                {
                        "Case 6.3", "POST - Add user category - Type: IN - Group: 2 - Parent: Default No Subcategory", "/category",
                        "102", "IN", String.valueOf(idInNoSub), "2"

                },
                {
                        "Case 6.4", "POST - Add user category - Type: IN - Group: 2 - Parent: Default Have Subcategory", "/category",
                        "101", "IN", String.valueOf(idINSub), "2"
                },
                {
                        "Case 6.5", "POST - Add user category - Type: OUT - Group: 1", "/category",
                        "103", "OUT", "0", "1"
                },
                {
                        "Case 6.6", "POST - Add user category - Type: OUT - Group: 2 - Parent: Was Created", "/category",
                        "103", "OUT", String.valueOf(id + 1), "2"
                },
                {
                        "Case 6.7", "POST - Add user category - Type: OUT - Group: 2 - Parent: Default No Subcategory", "/category",
                        "103", "OUT", String.valueOf(idOutNoSub), "2"
                },
                {
                        "Case 6.8", "POST - Add user category - Type: OUT - Group: 2 - Parent: Default Have Subcategory", "/category",
                        "103", "OUT", String.valueOf(idOutSub), "2"
                },
        };
    }

    //    @Test(dataProvider = "editUserCategoryTestData")
    public void editUserCategory(String name, String description, String path, String iconId, String type, String parentId, String levelGroup) throws IOException {
        id++;
        listEdit.add(id);
        String requestBody = """
                {
                    "iconId": %s,
                    "name": "%s",
                    "type" : "%s",
                    "parentId" : "%s"
                }""";
        String payload = String.format(requestBody, iconId, description, type, parentId);
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
        String expectedResponse = String.format(responseBody, id, description, type, iconId, parentId, levelGroup);


        // create test case
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category before add category";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountCategory, UserInfo.getPhoneNumber()), id - 301);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedResponse), List.of("time"));

        String desc3 = "Verify the number of count user category after add category";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountCategory, UserInfo.getPhoneNumber()), id - 300);


        String des9 = "Verify value of 'ID' field in SQL server is corrected";
        String query9 = String.format("SELECT MAX(ID) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s'", UserInfo.getPhoneNumber());
        TestAction step9 = querySimpleData(des9, query9, String.valueOf(id));

        String des4 = "Verify value of 'ICON_ID' field in SQL server is corrected";
        String query4 = String.format("SELECT ICON_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step4 = querySimpleData(des4, query4, iconId);

        String des5 = "Verify value of 'GROUP_NAME' field in SQL server is corrected";
        String query5 = String.format("SELECT GROUP_NAME FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step5 = querySimpleData(des5, query5, description);

        String des6 = "Verify value of 'CATEGORY_TYPE' field in SQL server is corrected";
        String query6 = String.format("SELECT CATEGORY_TYPE FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step6 = querySimpleData(des6, query6, type);

        String des7 = "Verify value of 'PARENT_ID' field in SQL server is corrected";
        String query7 = String.format("SELECT PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step7 = querySimpleData(des7, query7, parentId);

        String des8 = "Verify value of 'LEVEL_GROUP' field in SQL server is corrected";
        String query8 = String.format("SELECT LEVEL_GROUP FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step8 = querySimpleData(des8, query8, levelGroup);


        //add step & run
        tc.addStep(step1);
        tc.addStep(step2);
        tc.addStep(step3);
        tc.addStep(step9); // kiểm tra id
        tc.addStep(step4);
        tc.addStep(step5);
        tc.addStep(step6);
        tc.addStep(step7);
        tc.addStep(step8);
        tc.addStep(step9);
        tc.run();
    }

}
