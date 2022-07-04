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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UpdateTransactionTest extends AbstractExpenseManagementTest {

    private int transIdInMax = 0;
    private int transIdOutMax = 0;
    private int transIdInMin = 0;
    private int transIdOutMin = 0;

    @BeforeClass
    public void setup() throws SQLException {
        String queryGetDefaultMoneySource = "SELECT %s FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%s' and EXPENSE_TYPE = '%s'";
        transIdInMin += SQLHelper.executeQueryCount( String.format(queryGetDefaultMoneySource, "MIN(TRANS_ID)", UserInfo.getPhoneNumber(), "1"));
        transIdOutMin += SQLHelper.executeQueryCount( String.format(queryGetDefaultMoneySource,"MIN(TRANS_ID)", UserInfo.getPhoneNumber(), "-1"));
        transIdInMax += SQLHelper.executeQueryCount( String.format(queryGetDefaultMoneySource, "MAX(TRANS_ID)", UserInfo.getPhoneNumber(), "1"));
        transIdOutMax += SQLHelper.executeQueryCount( String.format(queryGetDefaultMoneySource, "MAX(TRANS_ID)", UserInfo.getPhoneNumber(), "-1"));
    }

    @DataProvider(name = "updateTransactionTestData")
    public Object[][] updateTransactionTestData() {
        return new Object[][]{
                {
                        "Case 13.1", "POST - UPDATE transaction - Type IN - Category: User added", "/transaction/edit",
                        transIdInMin, "1", "13000", "2022-07-01", 303
                },
                {
                        "Case 13.2", "POST - UPDATE transaction - Type OUT - Category: User added", "/transaction/edit",
                        transIdOutMin, "-1", "13000", "2022-07-01", 307
                },
                {
                        "Case 13.1", "POST - UPDATE transaction - Type IN - Category: User added", "/transaction/edit",
                        transIdInMax, "1", "13000", "2022-07-01", 77
                },
                {
                        "Case 13.2", "POST - UPDATE transaction - Type OUT - Category: User added", "/transaction/edit",
                        transIdOutMax, "-1", "13000", "2022-07-01", 4
                },
        };
    }

    @Test(dataProvider = "updateTransactionTestData", priority = 1)
    public void updateTransaction(String name, String description, String path, int transId, String expenseType, String manualAmount, String customTime, int expenseCategory) throws IOException, SQLException {
        String queryCountTransactions = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s'";
        String queryTransactionUpdated = "SELECT %s FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where TRANS_ID = '%s'";
        int totalTransactions = SQLHelper.executeQueryCount( String.format(queryCountTransactions, UserInfo.getPhoneNumber()));
        String requestBody = """
                 {
                    "expenseNote": "%s",
                    "manualAmount": %s,
                    "customTime": "%s",
                    "expenseCategory": %s,
                    "transId": %s
                }""";
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        customTime += " " + formattedDate;
        String payload = String.format(requestBody, description, manualAmount, customTime, expenseCategory, transId);
        String expectedTransaction = """
                "time": 1656643427300,
                "statusCode": 200,
                "errorCode": 0,
                "errorDes": null,
                "transaction": {
                    "expenseType": 0,
                    "sourceFrom": 0,
                    "userId": null,
                    "categoryId": 0,
                    "moneySourceId": 0,
                    "notes": null,
                    "amount": null,
                    "customTime": null,
                    "transCate": null,
                    "transId": %s
                }""";
        // create test case
        String expectedTransResponse = String.format(expectedTransaction, transId);
        TestCase tc = new TestCase(name, description);

        String desc1 = "Verify the number of count user category update add Transaction";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedTransResponse), List.of("time", "transId"));

        String desc3 = "Verify the number of count user category after update Transaction";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        String des4 = "Verify value of 'EXPENSE_TYPE' field in SQL server is corrected";
        String query4 = String.format(queryTransactionUpdated, "EXPENSE_TYPE", transId);
        TestAction step4 = querySimpleData(des4, query4, expenseType);

        String des5 = "Verify value of 'NOTE' field in SQL server is corrected";
        String query5 = String.format(queryTransactionUpdated, "NOTE", transId);
        TestAction step5 = querySimpleData(des5, query5, description);

        String des6 = "Verify value of 'AMOUNT' field in SQL server is corrected";
        String query6 = String.format(queryTransactionUpdated, "AMOUNT", transId);
        TestAction step6 = querySimpleData(des6, query6, manualAmount);

        String des7 = "Verify value of 'CUSTOM_TIME' field in SQL server is corrected";
        String query7 = String.format(queryTransactionUpdated, "CUSTOM_TIME", transId);
        TestAction step7 = querySimpleData(des7, query7, customTime + ".0");

        String des8 = "Verify value of 'CATEGORY_ID' field in SQL server is corrected";
        String query8 = String.format(queryTransactionUpdated, "CATEGORY_ID", transId);
        TestAction step8 = querySimpleData(des8, query8, String.valueOf(expenseCategory));

        String des9 = "Verify value of 'OWNER' field in SQL server is corrected";
        String query9 = String.format(queryTransactionUpdated, "OWNER", transId);
        TestAction step9 = querySimpleData(des9, query9, UserInfo.getPhoneNumber());


        //add step & run
        tc.addStep(step1);
        tc.addStep(step2);
        tc.addStep(step3);
        tc.addStep(step9);
        tc.addStep(step4);
        tc.addStep(step5);
        tc.addStep(step6);
        tc.addStep(step7);
        tc.addStep(step8);
        tc.run();
    }


}
