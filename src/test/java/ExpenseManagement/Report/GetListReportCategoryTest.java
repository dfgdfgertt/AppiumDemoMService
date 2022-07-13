package ExpenseManagement.Report;

import ExpenseManagement.AbstractExpenseManagementTest;
import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.UserInfo;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GetListReportCategoryTest extends AbstractExpenseManagementTest {
    String queryEachMonth = "SELECT COALESCE(SUM(AMOUNT),0) AMOUNT, COUNT(*) TOTAL_TRANS FROM SOAP_ADMIN.EXPENSE_TRANSACTION et LEFT JOIN SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr ON et.TRANS_ID = etr.TRANS_ID AND et.CATEGORY_ID = etr.CATEGORY_ID  where et.CATEGORY_ID  = %s AND et.OWNER ='%s' AND CUSTOM_TIME BETWEEN TIMESTAMP '%s' AND TIMESTAMP '%s'";

    String queryTime = "SELECT COALESCE(MIN(etr.CUSTOM_TIME), CURRENT_DATE) MIN, COALESCE(MAX(etr.CUSTOM_TIME), CURRENT_DATE) MAX  FROM SOAP_ADMIN.EXPENSE_TRANSACTION et LEFT JOIN SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr ON et.TRANS_ID = etr.TRANS_ID AND et.CATEGORY_ID = etr.CATEGORY_ID  where et.CATEGORY_ID  = %s AND et.OWNER ='%s'";

    int categoryIdWithoutTransaction;
    int categoryIdDefaultWithTransaction;
    int categoryIdUserAddedWithTransaction;

    @BeforeMethod
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
                        "Case 16.1", "GET - Get List Report Category - Category Default", "/category/report",
                        categoryIdDefaultWithTransaction
                },
                {
                        "Case 16.2", "GET - Get List Report Category - Category User Added", "/category/report",
                        categoryIdUserAddedWithTransaction
                },
                {
                        "Case 16.3", "GET - Get List Report Category - Category without transaction ", "/category/report",
                        categoryIdWithoutTransaction
                },
        };
    }

    @Test(dataProvider = "getReportCategoryCounterByIdTestData")
    public void getReportCategoryCounterByIdTest(String name, String description, String path, int categoryId) throws IOException {
        String requestBody = """
                {
                     "ids": "%s"
                 }""";
        String payload = String.format(requestBody, categoryId);
        List<String> listMonths = new ArrayList<>();
        List<String> listMonthsData = new ArrayList<>();
        List<String>  expectedData = new ArrayList<>();
        String expectedResponseFormat = """
                {
                     "userId": "%s",
                     "category": %s,
                     "month": "%s",
                     "numberTrans": %s,
                     "totalAmount": %s,
                     "percent": 0.0,
                     "categoryName": null
                 }""";
        JSONObject object = SQLHelper.executeQuery(String.format(queryTime, categoryId, UserInfo.getPhoneNumber())).getJSONObject(0);
        String str_date = object.get("MIN").toString().substring(0, 19);
        String end_date = object.get("MAX").toString().substring(0, 19);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(str_date, myFormatObj);
        LocalDateTime endDate = LocalDateTime.parse(end_date, myFormatObj);
        while (startDate.getYear() != endDate.getYear() || startDate.getMonth() != endDate.getMonth().plus(2)) {
            listMonths.add(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00")));
            listMonthsData.add(startDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            startDate = startDate.plusMonths(1);
        }
        for (int i = 1; i < listMonths.size(); i++) {
            JSONObject obj = SQLHelper.executeQuery(String.format(queryEachMonth, categoryId, UserInfo.getPhoneNumber(), listMonths.get(i - 1), listMonths.get(i))).getJSONObject(0);
            int totalAmount = obj.getInt("AMOUNT");
            int numberTrans = obj.getInt("TOTAL_TRANS");
            if (totalAmount != 0){
                expectedData.add(String.format(expectedResponseFormat,UserInfo.getPhoneNumber(), categoryId, listMonthsData.get(i-1),numberTrans,totalAmount));

            }
        }
        TestCase tc = new TestCase(name, description);
        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApiContains(desc2, path, signatureValue, payload, HttpMethod.POST, expectedData, null);

        //add step & run
        tc.addStep(step2);
        tc.run();
    }
}
