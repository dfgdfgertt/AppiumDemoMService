package ExpenseManagement.Transaction;

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

public class CreateTransactionTest extends AbstractExpenseManagementTest {

    int categoryIdDefaultOutHaveSub;
    int categoryIdDefaultOutNoSub;
    int categoryIdDefaultOutSub;

    int categoryIdUserAddedOutHaveSub;
    int categoryIdUserAddedOutNoSub;
    int categoryIdUserAddedOutSub;


    @BeforeClass
    public void setup() {
        String queryCategoryIdOutHaveSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s' AND ID IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%1$s' AND PARENT_ID IS NOT NULL)";
        String queryCategoryIdOutNoSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s' AND ID NOT IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%1$s' AND PARENT_ID IS NOT NULL)";
        String queryCategoryIdOutSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '2' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s'";
        categoryIdDefaultOutHaveSub = SQLHelper.executeQueryCount(String.format(queryCategoryIdOutHaveSub, "SYSTEM", "OUT"));
        categoryIdDefaultOutNoSub = SQLHelper.executeQueryCount(String.format(queryCategoryIdOutNoSub, "SYSTEM", "OUT"));
        categoryIdDefaultOutSub = SQLHelper.executeQueryCount(String.format(queryCategoryIdOutSub, "SYSTEM", "OUT"));

        categoryIdUserAddedOutHaveSub = SQLHelper.executeQueryCount(String.format(queryCategoryIdOutHaveSub, UserInfo.getPhoneNumber(), "OUT"));
        categoryIdUserAddedOutNoSub = SQLHelper.executeQueryCount(String.format(queryCategoryIdOutNoSub, UserInfo.getPhoneNumber(), "OUT"));
        categoryIdUserAddedOutSub = SQLHelper.executeQueryCount(String.format(queryCategoryIdOutSub, UserInfo.getPhoneNumber(), "OUT"));

    }

    @DataProvider(name = "addTransactionTestData")
    public Object[][] addTransactionTestData() {
        return new Object[][]{
                {
                        "Case 11.1", "POST - Add new transaction - Type OUT - Default category - Group 1 - Have subcategory", "/transaction",
                        "-1", randomAmount(), randomDate(), categoryIdDefaultOutHaveSub
                },
                {
                        "Case 11.2", "POST - Add new transaction - Type OUT - Default category - Group 1 - No subcategory", "/transaction",
                        "-1", randomAmount(), randomDate(), categoryIdDefaultOutNoSub
                },
                {
                        "Case 11.3", "POST - Add new transaction - Type OUT - Default category - Group 2 - Subcategory", "/transaction",
                        "-1", randomAmount(), randomDate(), categoryIdDefaultOutSub
                },
                {
                        "Case 11.4", "POST - Add new transaction - Type OUT - Category user added - Group 1 - Have subcategory", "/transaction",
                        "-1", randomAmount(), randomDate(), categoryIdUserAddedOutHaveSub
                },
                {
                        "Case 11.5", "POST - Add new transaction - Type OUT - Category user added - Group 1 - No subcategory", "/transaction",
                        "-1", randomAmount(), randomDate(), categoryIdUserAddedOutNoSub
                },
                {
                        "Case 11.6", "POST - Add new transaction - Type OUT - Category user added - Group 2 - Subcategory", "/transaction",
                        "-1", randomAmount(), randomDate(), categoryIdUserAddedOutSub
                },
        };
    }

    @Test(dataProvider = "addTransactionTestData", priority = 1)
    public void addTransaction(String name, String description, String path, String expenseType, String manualAmount, String customTime, int expenseCategory) throws IOException {
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
