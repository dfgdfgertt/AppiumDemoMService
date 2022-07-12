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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetListTransactionByMonthTest extends AbstractExpenseManagementTest {
    List<String> expectedResponse = new ArrayList<>();

    String queryTransaction = """
            SELECT etr.* FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr JOIN SOAP_ADMIN.EXPENSE_TRANSACTION et ON etr.TRANS_ID = et.TRANS_ID AND etr.CATEGORY_ID = et.CATEGORY_ID  WHERE etr.OWNER = '%s' AND CUSTOM_TIME BETWEEN TIMESTAMP '%s' AND TIMESTAMP '%s'
            """;

    @BeforeClass
    public void addTransactions() throws IOException {
        String queryListTransMonth = "SELECT COUNT(*) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr JOIN SOAP_ADMIN.EXPENSE_TRANSACTION et ON etr.TRANS_ID = et.TRANS_ID AND etr.CATEGORY_ID = et.CATEGORY_ID  WHERE etr.OWNER = '%s' AND CUSTOM_TIME BETWEEN TIMESTAMP '2022-06-01 00:00:00' AND TIMESTAMP '2022-07-01 00:00:00'";
        int num = SQLHelper.executeQueryCount(String.format(queryListTransMonth,UserInfo.getPhoneNumber()));
        while (num < 21) {
            if (addTransactionByMonth("OUT", "06", "2022")) {
                num++;
                System.out.println("Add transaction success");
            }
        }
    }


    @DataProvider(name = "getTransactionByMonthTestData")
    public Object[][] getTransactionByMonthTestData() {
        return new Object[][]{
                {
                        "Case 17.1", "GET - Get transaction by month form 0 max 5 trans - Month: 06-2022", "/transaction/getByCondition?index=%s&limitRow=%s&month=%s",
                        "4dNICZ23tH4gZloAr9Nd+fkGLz+w+MQ84EpCPWirRko=", "0", "5", "06-2022", true, 5
                },
                {
                        "Case 17.2", "GET - Get transaction by month form 0 max 20 trans - Month: 06-2022", "/transaction/getByCondition?index=%s&limitRow=%s&month=%s",
                        "Vbaq6ZZZ4pcWPEsbVVQArvKWXjATBaxjYUF34CDU2Rw=", "0", "20", "06-2022", true, 20
                },
                {
                        "Case 17.3", "GET - Get transaction by month form 1 max 20 trans - Month: 06-2022", "/transaction/getByCondition?index=%s&limitRow=%s&month=%s",
                        "f8pFxkpmd88Wd4mEBZJLhvIzgVpsU+tFsEXubf7n0cM=", "1", "20", "06-2022", true, 20
                },
                {
                        "Case 17.4", "GET - Get transaction by month form 10000 max 20 trans - Month: 06-2022", "/transaction/getByCondition?index=%s&limitRow=%s&month=%s",
                        "oWRw6kL0etDYz3G1VM6moe2zh1GAGG0Kv8m/DLDHoYM=", "10000", "20", "06-2022", false, 0
                },
                {
                        "Case 17.5", "GET - Get transaction by month with month not existed transaction - Month: 01-2021", "/transaction/getByCondition?index=%s&limitRow=%s&month=%s",
                        "kyPNQLlU/G+LGqFnFjC7anyaDFiYijEA8QFHuiM8QwU=", "0", "20", "01-2021", false, 0
                }
        };
    }

    @Test(dataProvider = "getTransactionByMonthTestData")
    public void getTransactionByMonthTest(String name, String description, String path, String signature, String index, String limitRow, String month, Boolean nextPage, int expectedNumber) throws IOException {
        String addPath = String.format(path, index, limitRow, month);
        String[] date = month.split("-");
        String startDate = String.format("%s-%s-01 00:00:00", date[1], date[0]);
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String endDate = LocalDateTime.parse(startDate, formatter).plusMonths(1).format(formatter);
        JSONArray transactionArray = SQLHelper.executeQuery(String.format(queryTransaction, UserInfo.getPhoneNumber(), startDate, endDate));
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
        for (int i = 0; i < Objects.requireNonNull(transactionArray).length(); i++) {
            JSONObject object = transactionArray.getJSONObject(i);
            // TRANS_ID, NOTE, AMOUNT, CATEGORY_ID, MONEY_SOURCE_ID, LAST_UPDATE, CREATED, EXPENSE_TYPE, CUSTOM_TIME
            int transId = object.getInt("TRANS_ID");
            String note = object.getString("NOTE");
            int amount = object.getInt("AMOUNT");
            int categoryId = object.getInt("CATEGORY_ID");
            int moneySourceId = object.getInt("MONEY_SOURCE_ID");
            String lastUpdate = object.get("LAST_UPDATE").toString().substring(0, 10);
            String created = object.get("CREATED").toString().substring(0, 10);
            int expenseType = object.getInt("EXPENSE_TYPE");
            String customTime = object.get("CUSTOM_TIME").toString().substring(0, object.get("CUSTOM_TIME").toString().length() - 2);
            String moneySource = String.format(expectedTransaction, UserInfo.getPhoneNumber(), transId, note, amount, categoryId, moneySourceId, lastUpdate, created, expenseType, customTime);
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
        TestAction step2 = sendApiContains(desc2, addPath, signature, null, HttpMethod.GET, nextPageResponse, null);

        String desc3 = "Verify number element of 'transactionData' is corrected";
        TestAction step3 = countElementResponse(desc3, addPath, signature, null, HttpMethod.GET, "transactionData", expectedNumber);

        //add step & run
        tc.addStep(step2);
        tc.addStep(step3);
        tc.run();
    }
}
