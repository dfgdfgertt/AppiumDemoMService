package ExpenseManagement;

import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.UserInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionTest extends AbstractExpenseManagementTest {
    private String simpleQuery = "SELECT " +
            "OWNER, TRANS_ID, NOTE, AMOUNT, CATEGORY_ID, COALESCE(MONEY_SOURCE_ID,0) MONEY_SOURCE_ID, LAST_UPDATE, CREATED, EXPENSE_TYPE, CUSTOM_TIME " +
            "FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%s' ORDER BY CUSTOM_TIME DESC OFFSET %s ROWS FETCH FIRST %s ROWS ONLY";

    @DataProvider(name = "getTransactionTestData")
    public Object[][] getTransactionTestData() {
        return new Object[][]{
                {
                        "Case 10.1", "GET - Get transaction by index form 0 to 5", "/transaction/get?index=%s&limitRow=%s",
                        "xNLVYTCV18HdH8MdkOLQh+WEXEE836pjKRaePIkxQpI=", "0", "5", true, 5
                },
                {
                        "Case 10.2", "GET - Get transaction by index form 0 to 20", "/transaction/get?index=%s&limitRow=%s",
                        "PLp9puoXh5MFdUJiNRC5T4SxgXoP8PqvNqCmCVzDYh8=", "0", "20", true, 20
                },
                {
                        "Case 10.3", "GET - Get transaction by index form 1 to 20", "/transaction/get?index=%s&limitRow=%s",
                        "Kv8mbz3qjusrIkp3lSVU5q15cQoEhwInnJxmeC0QeoM=", "1", "20", true, 20
                },
                {
                        "Case 10.4", "GET - Get transaction by index form 50 to 20", "/transaction/get?index=%s&limitRow=%s",
                        "00iQQHDK9zlI80tsg6QQAvQqA4Bie4d9oof5Q2lfOOE=", "50", "20", false, 0
                },
        };
    }

    @Test(dataProvider = "getTransactionTestData")
    public void getTransaction(String name, String description, String path, String signature, String index, String limitRow, Boolean nextPage, int expectedNumber) throws IOException, SQLException {
        String addPath = String.format(path, index, limitRow);
        String expectedTransaction = """
                {
                                             "owner": "%s",
                                             "transId": %s,
                                             "note": "%s",
                                             "amount": %s,
                                             "moneySource": 0,
                                             "categoryId": %s,
                                             "moneySourceId": %s,
                                             "moneySourceToId": 0,
                                             "lastUpdate": "%s",
                                             "created": "%s",
                                             "expenseType": %s,
                                             "customTime": "%s"
                                         }""";
        List<String> expectedResponse = new ArrayList<>();
        String query = String.format(simpleQuery, UserInfo.getPhoneNumber(), index, limitRow);
        JSONArray listMoneySource = SQLHelper.executeQuery(connection, query);
        for (int i = 0; i < (listMoneySource != null ? listMoneySource.length() : 0); i++) {
            JSONObject object = listMoneySource.getJSONObject(i);
            String owner = object.getString("OWNER");
            // OWNER, TRANS_ID, NOTE, AMOUNT, CATEGORY_ID, MONEY_SOURCE_ID, LAST_UPDATE, CREATED, EXPENSE_TYPE, CUSTOM_TIME
            int transId = object.getInt("TRANS_ID");
            String note = object.getString("NOTE");
            int amount = object.getInt("AMOUNT");
            int categoryId = object.getInt("CATEGORY_ID");
            int moneySourceId = object.getInt("MONEY_SOURCE_ID");
            String lastUpdate = object.get("LAST_UPDATE").toString().substring(0, 10);
            String created = object.get("CREATED").toString().substring(0, 10);
            int expenseType = object.getInt("EXPENSE_TYPE");
            String customTime = object.get("CUSTOM_TIME").toString().substring(0, object.get("CUSTOM_TIME").toString().length() - 2);
            String moneySource = String.format(expectedTransaction, owner, transId, note, amount, categoryId, moneySourceId, lastUpdate, created, expenseType, customTime);
            expectedResponse.add(moneySource);
        }

        // create test case
        TestCase tc = new TestCase(name, description);

        // create test step 1
        String desc2 = "Verify response data of request";
        String nextPageResponse = "\"nextPage\": true,";
        if (!nextPage) {
            nextPageResponse = "\"nextPage\": false,";
        }
        TestAction step2 = sendApiGetTransactionContains(desc2, addPath, signature, null, HttpMethod.GET, expectedResponse, nextPageResponse);

        String desc3 = "Verify number element of 'transactionData' is corrected";
        TestAction step3 = countElementResponse(desc3, addPath, signature, null, HttpMethod.GET, "transactionData", expectedNumber);

        //add step & run
        tc.addStep(step2);
        tc.addStep(step3);
        tc.run();
    }

    private int idCategoryIn = 0;
    private int idCategoryOut = 0;
    private int moneySource = 0;
    private String query = "";

    @BeforeClass
    public void setup(){

    }

    @DataProvider(name = "addTransactionTestData")
    public Object[][] addTransactionTestData() {
        return new Object[][]{
                {
                        "Case 5.1", "GET - Get transaction by index form 0 to 5", "/transaction",
                        "1", "15000", "5", true, 5
                }
        };
    }

//    @Test(dataProvider = "addTransactionTestData")
    public void addTransaction(String name, String description, String path, String signature, String index, String limitRow, Boolean nextPage, int expectedNumber) throws IOException, SQLException {
        String requestBody = """
                {
                    "expenseType": %s,
                    "expenseNote": "%s",
                    "manualAmount": %s,
                    "customTime": "%s",
                    "transCategoryMapping": "",
                    "expenseCategory": %s,
                    "moneySource": %s
                }""";
        String expectedTransaction = """
                {
                                             "owner": "%s",
                                             "transId": %s,
                                             "note": "%s",
                                             "amount": %s,
                                             "moneySource": 0,
                                             "categoryId": %s,
                                             "moneySourceId": %s,
                                             "moneySourceToId": 0,
                                             "lastUpdate": "%s",
                                             "created": "%s",
                                             "expenseType": %s,
                                             "customTime": "%s"
                                         }""";
        List<String> expectedResponse = new ArrayList<>();
        String query = String.format(simpleQuery, UserInfo.getPhoneNumber(), index, limitRow);
        JSONArray listMoneySource = SQLHelper.executeQuery(connection, query);
        for (int i = 0; i < (listMoneySource != null ? listMoneySource.length() : 0); i++) {
            JSONObject object = listMoneySource.getJSONObject(i);
            String owner = object.getString("OWNER");
            // OWNER, TRANS_ID, NOTE, AMOUNT, CATEGORY_ID, MONEY_SOURCE_ID, LAST_UPDATE, CREATED, EXPENSE_TYPE, CUSTOM_TIME
            int transId = object.getInt("TRANS_ID");
            String note = object.getString("NOTE");
            int amount = object.getInt("AMOUNT");
            int categoryId = object.getInt("CATEGORY_ID");
            int moneySourceId = object.getInt("MONEY_SOURCE_ID");
            String lastUpdate = object.get("LAST_UPDATE").toString().substring(0, 10);
            String created = object.get("CREATED").toString().substring(0, 10);
            int expenseType = object.getInt("EXPENSE_TYPE");
            String customTime = object.get("CUSTOM_TIME").toString().substring(0, object.get("CUSTOM_TIME").toString().length() - 2);
            String moneySource = String.format(expectedTransaction, owner, transId, note, amount, categoryId, moneySourceId, lastUpdate, created, expenseType, customTime);
            expectedResponse.add(moneySource);
        }

        // create test case
        TestCase tc = new TestCase(name, description);

        // create test step 1
        String desc2 = "Verify response data of request";
        String nextPageResponse = "\"nextPage\": true,";
        if (!nextPage) {
            nextPageResponse = "\"nextPage\": false,";
        }
        TestAction step2 = sendApiGetTransactionContains(desc2, path, signature, null, HttpMethod.GET, expectedResponse, nextPageResponse);

        String desc3 = "Verify number element of 'transactionData' is corrected";
        TestAction step3 = countElementResponse(desc3, path, signature, null, HttpMethod.GET, "transactionData", expectedNumber);

        //add step & run
        tc.addStep(step2);
        tc.addStep(step3);
        tc.run();
    }

}
