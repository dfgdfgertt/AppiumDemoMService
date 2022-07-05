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

public class DeleteTransactionTest extends AbstractExpenseManagementTest {

    private int transId = 0;

    @BeforeClass
    public void setup() {
        String queryGetDefaultMoneySource = "SELECT MIN(TRANS_ID) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%s'";
        transId += SQLHelper.executeQueryCount( String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber()));
    }

    @DataProvider(name = "deleteTransactionTestData")
    public Object[][] deleteTransactionTestData() {
        return new Object[][]{
                {
                        "Case 14", "POST - DELETE transaction - ", "/transaction/delete",
                        transId
                },
        };
    }

    @Test(dataProvider = "deleteTransactionTestData", priority = 1)
    public void deleteTransaction(String name, String description, String path, int transId) throws IOException {
        String queryCountTransactions = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s'";
        String queryTransactionDeleted = "SELECT %s FROM SOAP_ADMIN.EXPENSE_TRANSACTION where TRANS_ID = '%s'";
        int totalTransactions = SQLHelper.executeQueryCount( String.format(queryCountTransactions, UserInfo.getPhoneNumber()));
        String requestBody = """
                {
                   "transId": %s
                 }""";
        String payload = String.format(requestBody, transId);
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

        String desc1 = "Verify the number of count user category before delete Transaction";
        TestAction step1 = executeCountQueryDb(desc1, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, String.format(responseFormat, expectedTransResponse), List.of("time"));
        totalTransactions--;

        String desc3 = "Verify the number of count user category after delete Transaction";
        TestAction step3 = executeCountQueryDb(desc3, String.format(queryCountTransactions, UserInfo.getPhoneNumber()), totalTransactions);

        String des4 = "Verify value of 'OWNER' field in SQL server is corrected";
        String query4 = String.format(queryTransactionDeleted, "OWNER", transId);
        TestAction step4 = querySimpleData(des4, query4, UserInfo.getPhoneNumber() + "_DEL");

        //add step & run
        tc.addStep(step1);
        tc.addStep(step2);
        tc.addStep(step3);
        tc.addStep(step4);
        tc.run();
    }

}
