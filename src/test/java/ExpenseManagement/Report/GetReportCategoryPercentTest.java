package ExpenseManagement.Report;

import ExpenseManagement.AbstractExpenseManagementTest;
import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.UserInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.*;

public class GetReportCategoryPercentTest extends AbstractExpenseManagementTest {


    List<String> expectedResponseThisWeekIn = new ArrayList<>();
    List<String> expectedResponseThisMonth = new ArrayList<>();
    List<String> expectedResponseLast30days = new ArrayList<>();
    LocalDateTime myDateObj = LocalDateTime.now();
    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
    DateTimeFormatter monthObj = DateTimeFormatter.ofPattern("MM/yyyy");
    String monthOfYear = myDateObj.format(monthObj);

    String firstDayOfWeek = myDateObj.with(DayOfWeek.MONDAY).format(myFormatObj);
    String lastDayOfWeek = myDateObj.with(DayOfWeek.SUNDAY).plus(1, ChronoUnit.DAYS).format(myFormatObj);
    String firstDayOfMonth = myDateObj.withDayOfMonth(1).format(myFormatObj);
    String lastDayOfMonth = myDateObj.withDayOfMonth(1).plusMonths(1).minusDays(1).format(myFormatObj);
    String last30Days = myDateObj.minusDays(30).format(myFormatObj);
    String currentDay = myDateObj.plusDays(1).format(myFormatObj);
    String queryGetListCategory = """
            SELECT * FROM\s
            (SELECT SUM(CASE WHEN IO = -1 THEN -TOTAL_AMOUNT ELSE TOTAL_AMOUNT end) TOTAL_AMOUNT, 0 CATEGORY, 'NULL' NOTE, COUNT(*) TOTAL_TRANS FROM transhis_data_v2  where owner = '%1$s' AND IO = %2$s AND LAST_UPDATED BETWEEN TIMESTAMP '%3$s' AND TIMESTAMP '%4$s' GROUP BY 2,3) UNION\s
            (SELECT SUM(AMOUNT) TOTAL_AMOUNT, CATEGORY_ID CATEGORY, NOTE, COUNT(*) TOTAL_TRANS FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '%1$s' AND EXPENSE_TYPE = %2$s AND CUSTOM_TIME BETWEEN TIMESTAMP '%3$s' AND TIMESTAMP '%4$s' GROUP BY CATEGORY_ID,NOTE )
            ORDER BY 2\s
            """;

    //    @BeforeMethod
    public void getListTransactions() {
        JSONArray listTransThisWeekIn = SQLHelper.executeQuery(String.format(queryGetListCategory, UserInfo.getPhoneNumber(), "1", firstDayOfWeek, lastDayOfWeek));
        int total = 0;
        for (int i = 0; i < Objects.requireNonNull(listTransThisWeekIn).length(); i++) {
            JSONObject object = listTransThisWeekIn.getJSONObject(i);
            total += object.getInt("TOTAL_AMOUNT");
        }
        expectedResponseThisWeekIn.add("\"totalAmount\": " + total);
        JSONArray listTransThisMonth = SQLHelper.executeQuery(String.format(queryGetListCategory, UserInfo.getPhoneNumber(), firstDayOfMonth, lastDayOfMonth));
        JSONArray listTransLast30days = SQLHelper.executeQuery(String.format(queryGetListCategory, UserInfo.getPhoneNumber(), last30Days, currentDay));
        String expectedTransaction = """
                {
                     "userId": "%s",
                     "category": %s,
                     "month": %s,
                     "numberTrans": %s,
                     "totalAmount": %s,
                     "percent": %s,
                     "categoryName": "%s"
                 }""";
        for (int i = 0; i < Objects.requireNonNull(listTransThisWeekIn).length(); i++) {
            JSONObject object = listTransThisWeekIn.getJSONObject(i);
            int category = object.getInt("CATEGORY");
            String note = object.getString("NOTE");
            int totalAmount = object.getInt("TOTAL_AMOUNT");
            int totalTrans = object.getInt("TOTAL_TRANS");
            int percent = totalAmount * 100 / total;
            String transaction = String.format(expectedTransaction, UserInfo.getPhoneNumber(), category, "null", note, totalTrans, totalAmount, percent, note);
            expectedResponseThisWeekIn.add(transaction);
        }

    }

    @DataProvider(name = "getTransactionTestData")
    public Object[][] getTransactionTestData() {
        return new Object[][]{
                {
                        "Case 18.1", "GET - Get Report Category Percent - Type: IN -This Week ", "/report/ratio/category?reportType=%s&reportTime=%s",
                        "rwwe4BNXb+8E6MrdjbxPN3xEilREfxNi/cksKjIE08Y=", "1", "1", firstDayOfWeek, lastDayOfWeek, "null"
                },
                {
                        "Case 18.2", "GET - Get Report Category Percent - Type: IN - This Month", "/report/ratio/category?reportType=%s&reportTime=%s",
                        "kHJT2q9/pYv5Wo81tiReYqVQpj3LpkK2r3kEz9VtFVw=", "1", "2",  firstDayOfMonth, lastDayOfMonth, monthOfYear
                },
                {
                        "Case 18.3", "GET - Get Report Category Percent - Type: IN - Last 30 days", "/report/ratio/category?reportType=%s&reportTime=%s",
                        "ODcMjRaWThfRKWidn3AmY1Um+Q37slT44l5/R14LveI=", "1", "3",  last30Days, currentDay, "null"
                },
                {
                        "Case 18.4", "GET - Get Report Category Percent - Type: OUT -This Week ", "/report/ratio/category?reportType=%s&reportTime=%s",
                        "P3cJt+mEMn0XdGydJmqGwbonS3Nf27l+Sl+6fkXGZJI=", "-1", "1", firstDayOfWeek, lastDayOfWeek, "null"
                },
                {
                        "Case 18.5", "GET - Get Report Category Percent - Type: OUT - This Month", "/report/ratio/category?reportType=%s&reportTime=%s",
                        "K9ZJX5WoqYw/oIWmuSPIGnkl8v2u0txRrgeNCfSkCiY=", "-1", "2",  firstDayOfMonth, lastDayOfMonth, monthOfYear
                },
                {
                        "Case 18.6", "GET - Get Report Category Percent - Type: OUT - Last 30 days", "/report/ratio/category?reportType=%s&reportTime=%s",
                        "YEXLXULD3NN5p4H7y8Rdu/4CNOqwoGLupKcSI4Y8vAE=", "-1", "3",  last30Days, currentDay, "null"
                },

        };
    }

    @Test(dataProvider = "getTransactionTestData")
    public void getTransaction(String name, String description, String path, String signature, String reportType, String reportTime, String timeStart, String timeEnd, String monthValue) throws IOException {
        String addPath = String.format(path, reportType, reportTime);
        JSONArray listTransactions = SQLHelper.executeQuery(String.format(queryGetListCategory, UserInfo.getPhoneNumber(), reportType, timeStart, timeEnd));
        int total = 0;
        for (int i = 0; i < Objects.requireNonNull(listTransactions).length(); i++) {
            JSONObject object = listTransactions.getJSONObject(i);
            total += object.getInt("TOTAL_AMOUNT");
        }
        List<String> expectedResponse = new ArrayList<>();
        expectedResponse.add("\"totalAmount\": " + total);
        String expectedTransaction = """
                {
                     "userId": "%s",
                     "category": %s,
                     "month": %s,
                     "numberTrans": %s,
                     "totalAmount": %s,
                     "percent": %s,
                     "categoryName": "%s"
                 }""";
        for (int i = 0; i < Objects.requireNonNull(listTransactions).length(); i++) {
            JSONObject object = listTransactions.getJSONObject(i);
            int category = object.getInt("CATEGORY");
            String note = object.getString("NOTE");
            int totalAmount = object.getInt("TOTAL_AMOUNT");
            int totalTrans = object.getInt("TOTAL_TRANS");
            int percent = totalAmount * 100 / total;
            String transaction = String.format(expectedTransaction, UserInfo.getPhoneNumber(), category, monthValue, note, totalTrans, totalAmount, percent, note);
            expectedResponse.add(transaction);

            // create test case
            TestCase tc = new TestCase(name, description);

            // create test step 1
            String desc2 = "Verify response data of request";
            TestAction step2 = sendApiContains(desc2, addPath, signature, null, HttpMethod.GET, expectedResponse, null);

            //add step & run
            tc.addStep(step2);
            tc.run();
        }

    }
}
