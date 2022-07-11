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
import java.util.ArrayList;
import java.util.List;

public class DeleteTransactionTest extends AbstractExpenseManagementTest {
    List<Integer> categoryIdDefaultOutHaveSub;
    List<Integer> categoryIdDefaultOutNoSub;
    List<Integer> categoryIdDefaultOutSub;

    List<Integer> categoryIdUserAddedOutHaveSub;
    List<Integer> categoryIdUserAddedOutNoSub;
    List<Integer> categoryIdUserAddedOutSub;
    List<Integer> listTransId = new ArrayList<>();

    @BeforeClass
    public void setup() {
        String queryCategoryIdOutHaveSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s' AND ID IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%1$s' AND PARENT_ID IS NOT NULL)";
        String queryCategoryIdOutNoSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '1' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s' AND ID NOT IN (SELECT DISTINCT  PARENT_ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where user_id = '%1$s' AND PARENT_ID IS NOT NULL)";
        String queryCategoryIdOutSub =
                "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE LEVEL_GROUP = '2' AND user_id = '%1$s' AND CATEGORY_TYPE = '%2$s'";
        categoryIdDefaultOutHaveSub = SQLHelper.executeQueryGetListInt(String.format(queryCategoryIdOutHaveSub, "SYSTEM", "OUT"));
        categoryIdDefaultOutNoSub = SQLHelper.executeQueryGetListInt(String.format(queryCategoryIdOutNoSub, "SYSTEM", "OUT"));
        categoryIdDefaultOutSub = SQLHelper.executeQueryGetListInt(String.format(queryCategoryIdOutSub, "SYSTEM", "OUT"));

        categoryIdUserAddedOutHaveSub = SQLHelper.executeQueryGetListInt(String.format(queryCategoryIdOutHaveSub, UserInfo.getPhoneNumber(), "OUT"));
        categoryIdUserAddedOutNoSub = SQLHelper.executeQueryGetListInt(String.format(queryCategoryIdOutNoSub, UserInfo.getPhoneNumber(), "OUT"));
        categoryIdUserAddedOutSub = SQLHelper.executeQueryGetListInt(String.format(queryCategoryIdOutSub, UserInfo.getPhoneNumber(), "OUT"));
        String queryGetDefaultMoneySource = "SELECT MIN(TRANS_ID) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr where owner = '%s' AND CATEGORY_ID IN (%s)";
        listTransId.add(SQLHelper.executeQueryCount(String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber(), categoryIdDefaultOutHaveSub.toString().substring(1,categoryIdDefaultOutHaveSub.toString().length()-1))));
        listTransId.add(SQLHelper.executeQueryCount(String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber(), categoryIdDefaultOutNoSub.toString().substring(1,categoryIdDefaultOutNoSub.toString().length()-1))));
        listTransId.add(SQLHelper.executeQueryCount(String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber(), categoryIdDefaultOutSub.toString().substring(1,categoryIdDefaultOutSub.toString().length()-1))));
        listTransId.add(SQLHelper.executeQueryCount(String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber(), categoryIdUserAddedOutHaveSub.toString().substring(1,categoryIdUserAddedOutHaveSub.toString().length()-1))));
        listTransId.add(SQLHelper.executeQueryCount(String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber(), categoryIdUserAddedOutNoSub.toString().substring(1,categoryIdUserAddedOutNoSub.toString().length()-1))));
        listTransId.add(SQLHelper.executeQueryCount(String.format(queryGetDefaultMoneySource, UserInfo.getPhoneNumber(), categoryIdUserAddedOutSub.toString().substring(1,categoryIdUserAddedOutSub.toString().length()-1))));
    }

    @DataProvider(name = "deleteTransactionTestData")
    public Object[][] deleteTransactionTestData() {
        return new Object[][]{
//                {
//                        "Case 14", "POST - DELETE transaction", "/transaction/delete",
//                        listTransId.get(0)
//                },
                {
                        "Case 14.1", "POST - DELETE transaction - Type OUT - Default category - Group 1 - Have subcategory", "/transaction/delete",
                        listTransId.get(0)
                },
                {
                        "Case 14.2", "POST - DELETE transaction - Type OUT - Default category - Group 1 - No subcategory", "/transaction/delete",
                        listTransId.get(1)
                },
                {
                        "Case 14.3", "POST - DELETE transaction - Type OUT - Default category - Group 2 - Subcategory", "/transaction/delete",
                        listTransId.get(2)
                },
                {
                        "Case 14.4", "POST - DELETE transaction - Type OUT - Category user added - Group 1 - Have subcategory", "/transaction/delete",
                        listTransId.get(3)
                },
                {
                        "Case 14.5", "POST - DELETE transaction - Type OUT - Category user added - Group 1 - No subcategory", "/transaction/delete",
                        listTransId.get(4)
                },
                {
                        "Case 14.6", "POST - DELETE transaction - Type OUT - Category user added - Group 2 - Subcategory", "/transaction/delete",
                        listTransId.get(5)
                },
        };
    }

    @Test(dataProvider = "deleteTransactionTestData", priority = 1)
    public void deleteTransaction(String name, String description, String path, int transId) throws IOException {
        String queryCountTransactions = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s'";
        String queryTransactionDeleted = "SELECT %s FROM SOAP_ADMIN.EXPENSE_TRANSACTION where TRANS_ID = '%s'";
        int totalTransactions = SQLHelper.executeQueryCount(String.format(queryCountTransactions, UserInfo.getPhoneNumber()));
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
