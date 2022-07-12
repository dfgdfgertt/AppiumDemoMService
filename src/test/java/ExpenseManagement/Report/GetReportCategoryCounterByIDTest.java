package ExpenseManagement.Report;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GetReportCategoryCounterByIDTest extends AbstractExpenseManagementTest {

    String queryEachMonth = "SELECT COALESCE(SUM(AMOUNT),0) FROM SOAP_ADMIN.EXPENSE_TRANSACTION et LEFT JOIN SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr ON et.TRANS_ID = etr.TRANS_ID AND et.CATEGORY_ID = etr.CATEGORY_ID  where et.CATEGORY_ID  = %s AND et.OWNER ='%s' AND CUSTOM_TIME BETWEEN TIMESTAMP '%s' AND TIMESTAMP '%s'";

    String queryTime = "SELECT COALESCE(MIN(etr.CUSTOM_TIME), CURRENT_DATE) MIN, COALESCE(MAX(etr.CUSTOM_TIME), CURRENT_DATE) MAX  FROM SOAP_ADMIN.EXPENSE_TRANSACTION et LEFT JOIN SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr ON et.TRANS_ID = etr.TRANS_ID AND et.CATEGORY_ID = etr.CATEGORY_ID  where et.CATEGORY_ID  = %s AND et.OWNER ='%s'";

    int categoryIdWithoutTransaction;
    int categoryIdDefaultWithTransaction;
    int categoryIdUserAddedWithTransaction;

    @BeforeClass
    public void getCategoryWithoutTransaction() {
        String query1 = "SELECT COALESCE(MIN(ID),0) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE user_id= 'SYSTEM' AND ID IN (SELECT CATEGORY_ID FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%1$s' GROUP BY CATEGORY_ID)";
        categoryIdDefaultWithTransaction = SQLHelper.executeQueryCount(String.format(query1, UserInfo.getPhoneNumber()));
        String query2 = "SELECT COALESCE(MIN(ID),0) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE user_id= '%1$s' AND ID IN (SELECT CATEGORY_ID FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%1$s' GROUP BY CATEGORY_ID)";
        categoryIdUserAddedWithTransaction = SQLHelper.executeQueryCount(String.format(query2, UserInfo.getPhoneNumber()));
        String query3 = "SELECT COALESCE(MIN(ID),0) FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE user_id= 'SYSTEM' OR user_id = '%1$s' AND ID NOT IN (SELECT CATEGORY_ID FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%1$s' GROUP BY CATEGORY_ID)";
        categoryIdWithoutTransaction = SQLHelper.executeQueryCount(String.format(query3, UserInfo.getPhoneNumber()));
    }

    @DataProvider(name = "getReportCategoryCounterByIdTestData")
    public Object[][] getReportCategoryCounterByIdTestData() {
        return new Object[][]{
                {
                        "Case 21.1", "GET - Get Report Category Counter By ID - Category Default", "/category-counter/report",
                        categoryIdDefaultWithTransaction
                },
                {
                        "Case 21.2", "GET - Get Report Category Counter By ID - Category User Added", "/category-counter/report",
                        categoryIdUserAddedWithTransaction
                },
                {
                        "Case 21.3", "GET - Get Report Category Counter By ID - Category without transaction ", "/category-counter/report",
                        categoryIdWithoutTransaction
                },

        };
    }

    @Test(dataProvider = "getReportCategoryCounterByIdTestData")
    public void getReportCategoryCounterByIdTest(String name, String description, String path, int categoryId) throws IOException {
        String requestBody = """
                {
                     "id": %s
                 }""";
        String payload = String.format(requestBody, categoryId);
        List<String> listMonths = new ArrayList<>();
        List<String> listMonthsData = new ArrayList<>();
        StringBuilder expectedData = new StringBuilder();
        int total = 0;
        int totalFiveMonthAvgWithoutCurrAmount = 0;
        int totalSixMonthTotalAmount = 0;
        String expectedResponse = """
                "time": 1657508302279,
                        "statusCode": 200,
                        "errorCode": 0,
                        "errorDes": "",
                        "totalCurrentAmount": %s,
                        "fiveMonthAvgWithoutCurrAmount": %s.0,
                        "sixMonthTotalAmount": %s,
                        "data": {
                             %s
                             }
                        """;
        LocalDateTime endDate =  LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(5);
        while (startDate.getYear() != endDate.getYear() || startDate.getMonth() != endDate.getMonth().plus(2)) {
            listMonths.add(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00")));
            listMonthsData.add(startDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            startDate = startDate.plusMonths(1);
        }
        for (int i = 1; i < listMonths.size(); i++) {
            int amount = Integer.parseInt(SQLHelper.executeQueryGetOneString(String.format(queryEachMonth, categoryId, UserInfo.getPhoneNumber(), listMonths.get(i - 1), listMonths.get(i))));
            if (amount != 0){
                    total += amount;
                    totalSixMonthTotalAmount += amount;
                    if (i < (listMonths.size()-1)) {
                        totalFiveMonthAvgWithoutCurrAmount += amount;
                    }
                    expectedData.append("\"").append(listMonthsData.get(i - 1)).append("\": ").append(amount).append(",");
            }
        }
        if (!expectedData.isEmpty()) {
            expectedData.deleteCharAt(expectedData.length()-1);
        }
        String expectedTransResponse = String.format(responseFormat, String.format(expectedResponse, total, (totalFiveMonthAvgWithoutCurrAmount / 5), totalSixMonthTotalAmount, expectedData));
        TestCase tc = new TestCase(name, description);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, path, signatureValue, payload, HttpMethod.POST, expectedTransResponse, List.of("time"));

        //add step & run
        tc.addStep(step2);
        tc.run();
    }


}
