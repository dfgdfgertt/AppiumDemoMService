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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TransactionTest extends AbstractExpenseManagementTest {
    private final String simpleQuery = "SELECT " +
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
//                {
//                        "Case 10.4", "GET - Get transaction by index form 50 to 20", "/transaction/get?index=%s&limitRow=%s",
//                        "00iQQHDK9zlI80tsg6QQAvQqA4Bie4d9oof5Q2lfOOE=", "50", "20", false, 0
//                },
        };
    }

    @Test(dataProvider = "getTransactionTestData", priority = 2)
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
        JSONArray listTransaction = SQLHelper.executeQuery(connection, query);
        for (int i = 0; i < (listTransaction != null ? listTransaction.length() : 0); i++) {
            JSONObject object = listTransaction.getJSONObject(i);
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
    private int defaultMoneySource = 0;

    @BeforeClass
    public void setup() throws SQLException {
        String queryGetDefaultMoneySource = "SELECT COALESCE(MAX(ID),0) from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s'";
        defaultMoneySource += SQLHelper.executeQueryCount(connection, String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber()));
    }

    @DataProvider(name = "addTransactionTestData")
    public Object[][] addTransactionTestData() {
        return new Object[][]{
                {
                        "Case 11.1", "POST - Add new transaction - Type In - Default category - Group 1 - Have subcategory", "/transaction",
                        "1", "15000", "2022-05-14", 1, 1
                },
                {
                        "Case 11.2", "POST - Add new transaction - Type In - Default category -  Group 1 - No subcategory", "/transaction",
                        "1", "15000", "2022-06-14", 75, 1
                },
                {
                        "Case 11.3", "POST - Add new transaction - Type In - Category user added -  Group 1 - Have subcategory", "/transaction",
                        "1", "15000", "2022-07-14", 301, 1
                },
                {
                        "Case 11.4", "POST - Add new transaction - Type In - Category user added - Group 2 - Subcategory", "/transaction",
                        "1", "15000", "2022-01-14", 302, 1
                },
                {
                        "Case 11.5", "POST - Add new transaction - Type OUT - Default category - Group 1 - Have subcategory", "/transaction",
                        "-1", "15000", "2022-02-14", 3, 1
                },
                {
                        "Case 11.6", "POST - Add new transaction - Type OUT - Default category - Group 1 - No subcategory", "/transaction",
                        "-1", "15000", "2022-03-14", 2, 1
                },
                {
                        "Case 11.7", "POST - Add new transaction - Type OUT - Default category - Group 2 - Subcategory", "/transaction",
                        "-1", "15000", "2021-11-14", 7, 1
                },
                {
                        "Case 11.8", "POST - Add new transaction - Type OUT - Category user added - Group 1 - Have subcategory", "/transaction",
                        "-1", "15000", "2021-10-14", 305, 1
                },
                {
                        "Case 11.9", "POST - Add new transaction - Type OUT - Category user added - Group 2 - Subcategory", "/transaction",
                        "-1", "15000", "2021-09-14", 306, 1
                },

        };
    }

    @Test(dataProvider = "addTransactionTestData", priority = 1)
    public void addTransaction(String name, String description, String path, String expenseType, String manualAmount, String customTime, int expenseCategory, int moneySource) throws IOException, SQLException {
        String queryCountTransactions = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s'";
        String queryDetailTransactionByCustomTime = "SELECT %s FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s' AND CUSTOM_TIME = TIMESTAMP '%s'";
        int totalTransactions = SQLHelper.executeQueryCount(connection, String.format(queryCountTransactions, UserInfo.getPhoneNumber()));
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
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        customTime += " " + formattedDate;
        String payload = String.format(requestBody, expenseType, description, manualAmount, customTime, expenseCategory, moneySource);
        String expectedTransaction = """
                "time": 1656557618039,
                           "statusCode": 200,
                           "errorCode": 0,
                           "errorDes": null,
                           "transaction": {
                               "expenseType": %s,
                               "sourceFrom": 0,
                               "userId": "%s",
                               "categoryId": %s,
                               "moneySourceId": %s,
                               "notes": "%s",
                               "amount": %s,
                               "customTime": "%s",
                               "transCate": "",
                               "transId": -517601
                           }""";
        // create test case
        String expectedTransResponse = String.format(expectedTransaction, expenseType, UserInfo.getPhoneNumber(), expenseCategory, moneySource, description, manualAmount, customTime);
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category before add category";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedTransResponse), List.of("time", "transId"));
        totalTransactions++;

        String desc3 = "Verify the number of count user category after add category";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        String des4 = "Verify value of 'EXPENSE_TYPE' field in SQL server is corrected";
        String query4 = String.format(queryDetailTransactionByCustomTime, "EXPENSE_TYPE", UserInfo.getPhoneNumber(), customTime);
        TestAction step4 = querySimpleData(des4, query4, expenseType);

        String des5 = "Verify value of 'NOTE' field in SQL server is corrected";
        String query5 = String.format(queryDetailTransactionByCustomTime, "NOTE", UserInfo.getPhoneNumber(), customTime);
        TestAction step5 = querySimpleData(des5, query5, description);

        String des6 = "Verify value of 'AMOUNT' field in SQL server is corrected";
        String query6 = String.format(queryDetailTransactionByCustomTime, "AMOUNT", UserInfo.getPhoneNumber(), customTime);
        if (Objects.equals(expenseType, "-1")) {
            manualAmount = "-" + manualAmount;
        }
        TestAction step6 = querySimpleData(des6, query6, manualAmount);

        String des7 = "Verify value of 'CUSTOM_TIME' field in SQL server is corrected";
        String query7 = String.format(queryDetailTransactionByCustomTime, "CUSTOM_TIME", UserInfo.getPhoneNumber(), customTime);
        TestAction step7 = querySimpleData(des7, query7, customTime + ".0");

        String des8 = "Verify value of 'CATEGORY_ID' field in SQL server is corrected";
        String query8 = String.format(queryDetailTransactionByCustomTime, "CATEGORY_ID", UserInfo.getPhoneNumber(), customTime);
        TestAction step8 = querySimpleData(des8, query8, String.valueOf(expenseCategory));


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
