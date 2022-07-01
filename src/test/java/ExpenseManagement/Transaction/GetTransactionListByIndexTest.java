package ExpenseManagement.Transaction;

import ExpenseManagement.AbstractExpenseManagementTest;
import com.automation.test.TestAction;
import com.automation.test.TestCase;
import com.google.gson.JsonObject;
import constants.HttpMethod;
import helper.SQLHelper;
import object.TransactionInfo;
import object.UserInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetTransactionListByIndexTest extends AbstractExpenseManagementTest {
    List<String> expectedResponse = new ArrayList<>();

    @Test
    public void getListTransactions() throws SQLException {
        String queryTransaction = """
                SELECT * FROM\s
                (SELECT TRANS_ID, CASE WHEN IO = -1 THEN -TOTAL_AMOUNT ELSE TOTAL_AMOUNT end AMOUNT,LAST_UPDATED CUSTOM_TIME, IO EXPENSE_TYPE, 10000 MONEY_SOURCE, CREATED, LAST_UPDATED, 0 CATEGORY  FROM transhis_data_v2  where owner = '0909498114') UNION\s
                (SELECT TRANS_ID, AMOUNT, CUSTOM_TIME, EXPENSE_TYPE, 0 MONEY_SOURCE, CREATED, LAST_UPDATE, CATEGORY_ID CATEGORY FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '0909498114'AND CUSTOM_TIME < CURRENT_DATE)
                ORDER BY 3 DESC\s
                """;
        JSONArray transactionArray = SQLHelper.executeQuery(connection,String.format(queryTransaction, UserInfo.getPhoneNumber()));
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
        for (int i = 0; i < (transactionArray != null ? transactionArray.length() : 0); i++) {
            JSONObject object = transactionArray.getJSONObject(i);
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

    }
    @DataProvider(name = "getTransactionTestData")
    public Object[][] getTransactionTestData() {
        return new Object[][]{
                {
                        "Case 10.1", "GET - Get transaction by index form 0 max 5", "/transaction/get?index=%s&limitRow=%s",
                        "xNLVYTCV18HdH8MdkOLQh+WEXEE836pjKRaePIkxQpI=", "0", "5", true, 5
                },
                {
                        "Case 10.2", "GET - Get transaction by index form 0 max 20", "/transaction/get?index=%s&limitRow=%s",
                        "PLp9puoXh5MFdUJiNRC5T4SxgXoP8PqvNqCmCVzDYh8=", "0", "20", true, 20
                },
                {
                        "Case 10.3", "GET - Get transaction by index form 1 max 20", "/transaction/get?index=%s&limitRow=%s",
                        "Kv8mbz3qjusrIkp3lSVU5q15cQoEhwInnJxmeC0QeoM=", "1", "20", true, 20
                },
                {
                        "Case 10.4", "GET - Get transaction by index form 10000 max 20 trans", "/transaction/get?index=%s&limitRow=%s",
                        "qiJwm4Ek9vUtM72oLoGB63ybBufoIp8sWfcWmZzxAjg=", "10000", "20", false, 0
                },
                {
                        "Case 10.4", "GET - Get transaction by index form 1000000 max 20 trans", "/transaction/get?index=%s&limitRow=%s",
                        "ef1QgQ6YjshUeb8MlytBxlox7/iETK1jp1s7VDeOyRs=", "1000000", "20", false, 0
                },
        };
    }

    @Test(dataProvider = "getTransactionTestData", priority = 2)
    public void getTransaction(String name, String description, String path, String signature, String index, String limitRow, Boolean nextPage, int expectedNumber) throws IOException, SQLException {
        String addPath = String.format(path, index, limitRow);

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
}