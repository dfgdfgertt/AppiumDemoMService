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
import java.util.Random;

public class UpdateTransactionTest extends AbstractExpenseManagementTest {

    List<Integer> categoryIdDefaultOutHaveSub;
    List<Integer> categoryIdDefaultOutNoSub;
    List<Integer> categoryIdDefaultOutSub;

    List<Integer> categoryIdUserAddedOutHaveSub;
    List<Integer> categoryIdUserAddedOutNoSub;
    List<Integer> categoryIdUserAddedOutSub;
    List<Integer> listTrans = new ArrayList<>();

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

        String queryGetDefaultMoneySource = "SELECT %s FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%s' and EXPENSE_TYPE = '%s'";
        listTrans.addAll(SQLHelper.executeQueryGetListInt( String.format(queryGetDefaultMoneySource,"TRANS_ID", UserInfo.getPhoneNumber(), "-1")));
    }

    @DataProvider(name = "updateTransactionTestData")
    public Object[][] updateTransactionTestData() {
        return new Object[][]{
                {
                        "Case 14.1", "POST - UPDATE transaction - Type OUT - Default category - Group 1 - Have subcategory", "/transaction/edit",
                        listTrans.get(0), "-1", randomAmount(), randomDate(), categoryIdDefaultOutHaveSub.get(new Random().nextInt(categoryIdDefaultOutHaveSub.size()))
                },
                {
                        "Case 14.2", "POST - UPDATE transaction - Type OUT - Default category - Group 1 - No subcategory", "/transaction/edit",
                        listTrans.get(1), "-1", randomAmount(), randomDate(), categoryIdDefaultOutNoSub.get(new Random().nextInt(categoryIdDefaultOutNoSub.size()))
                },
                {
                        "Case 14.3", "POST - UPDATE transaction - Type OUT - Default category - Group 2 - Subcategory", "/transaction/edit",
                        listTrans.get(2),"-1", randomAmount(), randomDate(), categoryIdDefaultOutSub.get(new Random().nextInt(categoryIdDefaultOutSub.size()))
                },
                {
                        "Case 14.4", "POST - UPDATE transaction - Type OUT - Category user added - Group 1 - Have subcategory", "/transaction/edit",
                        listTrans.get(3),"-1", randomAmount(), randomDate(), categoryIdUserAddedOutHaveSub.get(new Random().nextInt(categoryIdUserAddedOutHaveSub.size()))
                },
                {
                        "Case 14.5", "POST - UPDATE transaction - Type OUT - Category user added - Group 1 - No subcategory", "/transaction/edit",
                        listTrans.get(4), "-1", randomAmount(), randomDate(), categoryIdUserAddedOutNoSub.get(new Random().nextInt(categoryIdUserAddedOutNoSub.size()))
                },
                {
                        "Case 14.6", "POST - UPDATE transaction - Type OUT - Category user added - Group 2 - Subcategory", "/transaction/edit",
                        listTrans.get(5),"-1", randomAmount(), randomDate(), categoryIdUserAddedOutSub.get(new Random().nextInt(categoryIdUserAddedOutSub.size()))
                },
        };
    }

    @Test(dataProvider = "updateTransactionTestData")
    public void updateTransaction(String name, String description, String path, int transId, String expenseType, String manualAmount, String customTime, int expenseCategory) throws IOException {
        String queryCountTransactions = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF where owner = '%s'";
        String queryTransactionUpdated = "SELECT etr.%s FROM SOAP_ADMIN.EXPENSE_TRANSACTION et JOIN SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr ON et.TRANS_ID = etr.TRANS_ID AND et.CATEGORY_ID = etr.CATEGORY_ID where et.TRANS_ID  = %s\n";
        int totalTransactions = SQLHelper.executeQueryCount( String.format(queryCountTransactions, UserInfo.getPhoneNumber()));
        String requestBody = """
                 {
                    "expenseNote": "%s",
                    "manualAmount": %s,
                    "customTime": "%s",
                    "expenseCategory": %s,
                    "transId": %s
                }""";
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
