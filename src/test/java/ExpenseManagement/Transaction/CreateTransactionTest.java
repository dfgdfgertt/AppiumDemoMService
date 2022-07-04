package ExpenseManagement.Transaction;

import ExpenseManagement.AbstractExpenseManagementTest;
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
import java.util.List;
import java.util.Objects;

public class CreateTransactionTest extends AbstractExpenseManagementTest {

    private int idCategoryIn = 0;
    private int idCategoryOut = 0;
    private int moneySource = 0;
    private int defaultMoneySource = 0;

    @BeforeClass
    public void setup() throws SQLException {
        String queryGetDefaultMoneySource = "SELECT COALESCE(MAX(ID),0) from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s'";
        defaultMoneySource += SQLHelper.executeQueryCount(String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber()));
    }

    @DataProvider(name = "addTransactionTestData")
    public Object[][] addTransactionTestData() {
        return new Object[][]{
                {
                        "Case 11.1", "POST - Add new transaction - Type In - Default category - Group 1 - Have subcategory", "/transaction",
                        "1", "15000", "2022-05-14", 1
                },
                {
                        "Case 11.2", "POST - Add new transaction - Type In - Default category -  Group 1 - No subcategory", "/transaction",
                        "1", "15000", "2022-07-03", 75
                },
                {
                        "Case 11.3", "POST - Add new transaction - Type In - Category user added -  Group 1 - Have subcategory", "/transaction",
                        "1", "15000", "2022-07-04", 301
                },
                {
                        "Case 11.4", "POST - Add new transaction - Type In - Category user added - Group 2 - Subcategory", "/transaction",
                        "1", "15000", "2022-01-14", 302
                },
                {
                        "Case 11.5", "POST - Add new transaction - Type OUT - Default category - Group 1 - Have subcategory", "/transaction",
                        "-1", "20000", "2022-02-14", 3
                },
                {
                        "Case 11.6", "POST - Add new transaction - Type OUT - Default category - Group 1 - No subcategory", "/transaction",
                        "-1", "20000", "2022-07-04", 2
                },
                {
                        "Case 11.7", "POST - Add new transaction - Type OUT - Default category - Group 2 - Subcategory", "/transaction",
                        "-1", "20000", "2021-11-14", 7
                },
                {
                        "Case 11.8", "POST - Add new transaction - Type OUT - Category user added - Group 1 - Have subcategory", "/transaction",
                        "-1", "20000", "2022-06-13", 305
                },
                {
                        "Case 11.9", "POST - Add new transaction - Type OUT - Category user added - Group 2 - Subcategory", "/transaction",
                        "-1", "20000", "2022-07-04", 306
                },
        };
    }

    @Test(dataProvider = "addTransactionTestData", priority = 1)
    public void addTransaction(String name, String description, String path, String expenseType, String manualAmount, String customTime, int expenseCategory) throws IOException, SQLException {
        String queryCountTransactions = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s'";
        String queryDetailTransactionByCustomTime = "SELECT %s FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s' AND CUSTOM_TIME = TIMESTAMP '%s'";
        int totalTransactions = SQLHelper.executeQueryCount(String.format(queryCountTransactions, UserInfo.getPhoneNumber()));
        String requestBody = """
                {
                    "expenseType": %s,
                    "expenseNote": "%s",
                    "manualAmount": %s,
                    "customTime": "%s",
                    "transCategoryMapping": "",
                    "expenseCategory": %s,
                    "moneySource": 0
                }""";
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        customTime += " " + formattedDate;
        String payload = String.format(requestBody, expenseType, description, manualAmount, customTime, expenseCategory);
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
                               "moneySourceId": 0,
                               "notes": "%s",
                               "amount": %s,
                               "customTime": "%s",
                               "transCate": "",
                               "transId": -517601
                           }""";
        // create test case
        String expectedTransResponse = String.format(expectedTransaction, expenseType, UserInfo.getPhoneNumber(), expenseCategory, description, manualAmount, customTime);
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category before add Transaction";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedTransResponse), List.of("time", "transId"));
        totalTransactions++;

        String desc3 = "Verify the number of count user category after add Transaction";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        String des4 = "Verify value of 'EXPENSE_TYPE' field in SQL server is corrected";
        String query4 = String.format(queryDetailTransactionByCustomTime, "EXPENSE_TYPE", UserInfo.getPhoneNumber(), customTime);
        TestAction step4 = querySimpleData(des4, query4, expenseType);

        String des5 = "Verify value of 'NOTE' field in SQL server is corrected";
        String query5 = String.format(queryDetailTransactionByCustomTime, "NOTE", UserInfo.getPhoneNumber(), customTime);
        TestAction step5 = querySimpleData(des5, query5, description);

        String des6 = "Verify value of 'AMOUNT' field in SQL server is corrected";
        String query6 = String.format(queryDetailTransactionByCustomTime, "AMOUNT", UserInfo.getPhoneNumber(), customTime);
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
