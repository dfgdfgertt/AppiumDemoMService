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

public class MoneySourceTest extends AbstractExpenseManagementTest {
    private List<Integer> listEdit = new ArrayList<>();

    private JSONArray listMoneySource;

    @BeforeClass
    public void setup() throws SQLException {
        String query = """
                select\s
                \tmns.ID,
                \tCOALESCE(MONEY_SOURCE_TYPE,'null') MONEY_SOURCE_TYPE,
                \tAMOUNT,
                \tCOALESCE(ICON_ID,0) ICON_ID,
                \tCOALESCE(MONEY_SOURCE_NAME,'null') MONEY_SOURCE_NAME,
                \tCOALESCE(GROUP_MONEY_SOURCE,0) GROUP_MONEY_SOURCE,
                \tCOALESCE(MONEY_SOURCE_CREDIT,0) MONEY_SOURCE_CREDIT,
                \tCREDIT_AVAILABLE,
                \tCOALESCE(NAME,'null') NAME,
                \tCASE\s
                \t\tWHEN mns.ICON_LINK IS NULL\s
                \t\tTHEN emi.ICON_LINK
                \t\tELSE mns.ICON_LINK
                \tEND ICON_LINK
                from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE mns\s
                left JOIN SOAP_ADMIN.EXPENSE_MANAGEMENT_GROUP_MONEY_SOURCE emgms\s
                ON MNS.GROUP_MONEY_SOURCE = EMGMS.ID \s
                LEFT JOIN SOAP_ADMIN.EXPENSE_MANAGEMENT_ICON emi
                ON  mns.ICON_ID = emi.ID
                where user_id = '0909498114'""";
        listMoneySource = SQLHelper.executeQuery(connection, query);
    }

    @DataProvider(name = "getMoneySourceTestData")
    public Object[][] getMoneySourceTestData() {
        return new Object[][]{
                {
                        "Case 7", "GET - Get all money source"

                }
        };
    }

    @Test(dataProvider = "getMoneySourceTestData", priority = 2)
    public void getMoneySource(String name, String description) throws IOException {
        // create test case
        TestCase tc = new TestCase(name, description);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < listMoneySource.length(); i++) {
            JSONObject object = listMoneySource.getJSONObject(i);
            String moneySourceType = object.getString("MONEY_SOURCE_TYPE");
            Long amount = object.getLong("AMOUNT");
            int iconId = object.getInt("ICON_ID");
            String moneySourceName = object.getString("MONEY_SOURCE_NAME");
            int groupMoneySource = object.getInt("GROUP_MONEY_SOURCE");
            int id = object.getInt("ID");
            String userId = UserInfo.getPhoneNumber();
            int moneySourceCredit = object.getInt("MONEY_SOURCE_CREDIT");
            int creditAvailable = object.getInt("CREDIT_AVAILABLE");
            String iconLink = object.getString("ICON_LINK");
            String groupMoneySourceName = object.getString("NAME");
            if (groupMoneySourceName.equals("null")) {
                groupMoneySourceName = "";
            }
            if (!moneySourceType.equals("null")) {
                moneySourceType = "\"" + moneySourceType + "\"";
            }
            String moneySourceResponse = """
                    {
                                    "idNew": 0,
                                    "moneySourceType": %s,
                                    "amount": %s,
                                    "iconId": %s,
                                    "moneySourceName": "%s",
                                    "groupMoneySource": %s,
                                    "extras": null,
                                    "id": %s,
                                    "userId": "%s",
                                    "moneySourceCredit": %s,
                                    "creditAvailable": %s,
                                    "isDeleted": 0,
                                    "iconLink": "%s",
                                    "groupMoneySourceName": "%s",
                                    "moneySourceNameEn": null,
                                    "parentId": null
                                }""";
            String moneySource = String.format(moneySourceResponse, moneySourceType, amount, iconId, moneySourceName, groupMoneySource, id, userId, moneySourceCredit, creditAvailable, iconLink, groupMoneySourceName);
            list.add(moneySource);
        }
        // create test step 1
        String desc1 = "Verify response data of request";
        String path = "/money-source";
        TestAction step1 = sendApiContains(desc1, path, signatureValue, null, HttpMethod.GET, list, null);
        // actual

        //add step & run
        tc.addStep(step1);
        tc.run();
    }

    String queryCountMoneySource = "select COUNT(*) from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND MONEY_SOURCE_TYPE = 'USER_CREATED'";
    String queryCountAllMoneySource = "select COUNT(*) from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s'";
    private int id = 0;
    private int totalMoneySource = 0;

    @BeforeClass
    public void setupCountQueryMoneySource() throws SQLException {
        id += SQLHelper.executeQueryCount(connection, String.format(queryCountMoneySource, UserInfo.getPhoneNumber()));
        totalMoneySource += SQLHelper.executeQueryCount(connection, String.format(queryCountAllMoneySource, UserInfo.getPhoneNumber()));
    }

    @DataProvider(name = "addUserCategoryTestData")
    public Object[][] addUserCategoryTestData() {
        return new Object[][]{
                {
                        "Case 8.1", "POST - Add New Money Source When Group Id = 1 - Ví điện tử", "/money-source",
                        "11", "1", "150000", "0", "0","Ví điện tử"
                },
                {
                        "Case 8.2", "POST - Add New Money Source When Group Id = 2 - Tiền mặt", "/money-source",
                        "11", "2", "200000", "0", "0","Tiền mặt"
                },
                {
                        "Case 8.3", "POST - Add New Money Source When Group Id = 3 - Tài khoản ngân hàng", "/money-source",
                        "11", "3", "250000", "0", "0","Tài khoản ngân hàng"
                },
                {
                        "Case 8.4", "POST - Add New Money Source When Group Id = 4 - Thẻ ghi nợ", "/money-source",
                        "11", "4", "300000", "0", "0","Thẻ ghi nợ"
                },
                {
                        "Case 8.5", "POST - Add New Money Source When Group Id = 5 - Thẻ tín dụng", "/money-source",
                        "11", "5", "350000", "1", "350000","Thẻ tín dụng"
                },
                {
                        "Case 8.6", "POST - Add New Money Source When Group Id = 6 - Khác", "/money-source",
                        "11", "6", "400000", "0", "0","Khác"
                },
        };
    }

    @Test(dataProvider = "addUserCategoryTestData", priority = 1)
    public void addUserCategoryTest(String name, String description, String path, String iconId, String groupId, String amount, String moneySourceCredit, String creditAvailable, String groupMoneySourceName) throws IOException {
        id++;
        totalMoneySource++;
        listEdit.add(id);
        String requestBody = """
                {
                  "name": "%s",
                  "iconId": %s,
                  "groupId": %s,
                  "amount": %s,
                  "moneySourceCredit": %s,
                  "creditAvailable": %s
                }""";
        String payload = String.format(requestBody, description, iconId, groupId, amount, moneySourceCredit, creditAvailable);
        String moneySourceResponse = """
                "time": 1656401030335,
                        "statusCode": 200,
                        "errorCode": 0,
                        "errorDes": null,
                        "moneySources": null,
                        "moneySource": {
                            "idNew": 0,
                            "moneySourceType": null,
                            "amount": %s,
                            "iconId": %s,
                            "moneySourceName": "%s",
                            "groupMoneySource": %s,
                            "extras": null,
                            "id": %s,
                            "userId": "%s",
                            "moneySourceCredit":%s,
                            "creditAvailable": %s,
                            "isDeleted": 0,
                            "iconLink": "https://img.mservice.com.vn/momo_app_v2/new_version/appx_expense/image/travel.png",
                            "groupMoneySourceName": "%s",
                            "moneySourceNameEn": null,
                            "parentId": null
                        },
                        "blacklistEdit": null""";
        String expectedResponse = String.format(moneySourceResponse,amount,iconId, description,groupId, id, UserInfo.getPhoneNumber(), moneySourceCredit, creditAvailable,groupMoneySourceName);


        // create test case
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category before add money source";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountAllMoneySource, UserInfo.getPhoneNumber()), totalMoneySource-1);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedResponse), List.of("time", "iconLink"));

        String desc3 = "Verify the number of count user category after add money source";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountAllMoneySource, UserInfo.getPhoneNumber()), totalMoneySource );


        String des9 = "Verify value of 'ID' field in SQL server is corrected";
        String query9 = String.format("SELECT MAX(ID) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND MONEY_SOURCE_TYPE = 'USER_CREATED'", UserInfo.getPhoneNumber());
        TestAction step9 = querySimpleData(des9, query9, String.valueOf(id));

        String des4 = "Verify value of 'ICON_ID' field in SQL server is corrected";
        String query4 = String.format("SELECT ICON_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step4 = querySimpleData(des4, query4, iconId);

        String des5 = "Verify value of 'MONEY_SOURCE_NAME' field in SQL server is corrected";
        String query5 = String.format("SELECT MONEY_SOURCE_NAME FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step5 = querySimpleData(des5, query5, description);

        String des6 = "Verify value of 'AMOUNT' field in SQL server is corrected";
        String query6 = String.format("SELECT AMOUNT FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step6 = querySimpleData(des6, query6, amount);

        String des7 = "Verify value of 'GROUP_MONEY_SOURCE' field in SQL server is corrected";
        String query7 = String.format("SELECT GROUP_MONEY_SOURCE FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step7 = querySimpleData(des7, query7, groupId);

        String des8 = "Verify value of 'MONEY_SOURCE_CREDIT' field in SQL server is corrected";
        String query8 = String.format("SELECT MONEY_SOURCE_CREDIT FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step8 = querySimpleData(des8, query8, moneySourceCredit);

        String des10 = "Verify value of 'CREDIT_AVAILABLE' field in SQL server is corrected";
        String query10  = String.format("SELECT CREDIT_AVAILABLE FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' AND ID='%s'", UserInfo.getPhoneNumber(), id);
        TestAction step10  = querySimpleData(des10, query10, creditAvailable);

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
        tc.addStep(step10);
        tc.run();
    }

}
