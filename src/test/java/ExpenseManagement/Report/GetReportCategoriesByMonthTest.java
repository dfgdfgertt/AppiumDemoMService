package ExpenseManagement.Report;

import ExpenseManagement.AbstractExpenseManagementTest;
import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.UserInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetReportCategoriesByMonthTest extends AbstractExpenseManagementTest {
//    String queryGetListCategory = "SELECT * FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  WHERE owner = '%s' AND CUSTOM_TIME BETWEEN TIMESTAMP '%s' AND TIMESTAMP '%s'";

    //    String queryGetListCategory = "SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE user_id= 'SYSTEM' OR user_id = '0909498114' AND ID IN (SELECT CATEGORY_ID FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  where owner = '0909498114' GROUP BY CATEGORY_ID) AND LEVEL_GROUP = 1";
    String queryGetListAmount = "SELECT COALESCE(SUM(AMOUNT),0) FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  WHERE owner = '%1$s' AND CUSTOM_TIME BETWEEN TIMESTAMP '%3$s' AND TIMESTAMP '%4$s' AND CATEGORY_ID = %2$s AND CATEGORY_ID IN (SELECT ID FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE user_id= 'SYSTEM' OR user_id = '%1$s' AND PARENT_ID = %2$s)";
    String queryGetListReport = """
            SELECT table1.CATEGORY_ID CATEGORY_ID, table2.GROUP_NAME GROUP_NAME, COALESCE(table2.GROUP_NAME_EN,'null') GROUP_NAME_EN, table2.ICON_ID ICON_ID, emi.ICON_LINK FROM\s
            (SELECT CATEGORY_ID FROM SOAP_ADMIN.EXPENSE_TRANSACTION_REF etr  WHERE owner = '%1$s' AND CUSTOM_TIME BETWEEN TIMESTAMP '%2$s' AND TIMESTAMP '%3$s' GROUP BY CATEGORY_ID) table1
            JOIN
            (SELECT * FROM SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP WHERE (user_id= 'SYSTEM' OR user_id = '%1$s') AND LEVEL_GROUP = 1) table2
            ON table1.CATEGORY_ID = table2.ID
            JOIN\s
            SOAP_ADMIN.EXPENSE_MANAGEMENT_ICON emi
            ON \s
            table2.ICON_ID = emi.ID""";


    public  List<String> getListExpectedResponse(String time){
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeDateTime = LocalDateTime.parse(String.format("%s 00:00:00",time), myFormatObj);
        String timeStart = timeDateTime.format(myFormatObj);
        String timeEnd = timeDateTime.plusMonths(1).format(myFormatObj);
        String timePrevious = timeDateTime.minusMonths(1).format(myFormatObj);
        List<String> expectedResponse = new ArrayList<>();

        JSONArray listTransactions = SQLHelper.executeQuery(String.format(queryGetListReport, UserInfo.getPhoneNumber(), timeStart, timeEnd));
        String expectedTransaction = """
                {
                                   "id": %s,
                                   "groupName": "%s",
                                   "groupNameEn": "%s",
                                   "amount": %s,
                                   "percentage": %s,
                                   "iconId": %s,
                                   "iconLink": "%s"
                               }""";
        for (int i = 0; i < Objects.requireNonNull(listTransactions).length(); i++) {
            JSONObject object = listTransactions.getJSONObject(i);
            int categoryId = object.getInt("CATEGORY_ID");
            String groupName = object.getString("GROUP_NAME");
            String groupNameEn = object.getString("GROUP_NAME_EN");
            if (groupNameEn.equals("null")){
                groupNameEn= "";
            }
            int totalCurrentAmount = SQLHelper.executeQueryCount(String.format(queryGetListAmount, UserInfo.getPhoneNumber(), categoryId, timeStart, timeEnd));
            int totalPreviousAmount = SQLHelper.executeQueryCount(String.format(queryGetListAmount, UserInfo.getPhoneNumber(), categoryId, timePrevious, timeStart));
            float percentage = 0;
            if (totalPreviousAmount != 0) {
                percentage = (float) (totalCurrentAmount - totalPreviousAmount) / totalPreviousAmount;

            }
            int iconId = object.getInt("ICON_ID");
            String iconLink = object.getString("ICON_LINK");
            String transaction = String.format(expectedTransaction, categoryId, groupName, groupNameEn, totalCurrentAmount, percentage, iconId, iconLink);
            expectedResponse.add(transaction);
        }
        return expectedResponse;
    }

    @DataProvider(name = "getReportCategoriesByMonthTestData")
    public Object[][] getReportCategoriesByMonthTestData() {
        return new Object[][]{
                {
                        "Case 23.1", "GET - Get Report Categories By Month - Type: OUT - Current month -07-2022", "/report/categories?month=%s",
                        "dE1eoRvOMDP2VD44oGWr9whjU2nvyPsUbAwN2pqMlog=", "2022-07-01"
                },
                {
                        "Case 23.2", "GET - Get Report Categories By Month - Type: OUT - Previous month - 06-2022 ", "/report/categories?month=%s",
                        "xXuChjDzodG8bdHfAhAcchNDZgIjOm1zMUQwp0mRm7U=", "2022-06-01"
                },
        };
    }

    @Test(dataProvider = "getReportCategoriesByMonthTestData")
    public void getReportCategoriesByMonthTest(String name, String description, String path, String signature, String time) throws IOException {
        String addPath = String.format(path, time);
        List<String> expectedResponse = getListExpectedResponse(time);
        // create test case
        TestCase tc = new TestCase(name, description);

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApiContains(desc2, addPath, signature, null, HttpMethod.GET, expectedResponse, null);

        //add step & run
        tc.addStep(step2);
        tc.run();
    }

    @DataProvider(name = "getReportCategoriesByMonthNullTestData")
    public Object[][] getReportCategoriesByMonthNullTestData() {
        return new Object[][]{
                {
                        "Case 23.3", "GET - Get Report Categories By Month - Type: OUT - month not exist transaction - 01-2021 ", "/report/categories?month=%s",
                        "q/Ok92SmnH4CBJo02VkqCt55YDfgitIackKOaThjZ8s=", "2021-01-01"
                },

        };
    }

    @Test(dataProvider = "getReportCategoriesByMonthNullTestData")
    public void getReportCategoriesByMonthNullTest(String name, String description, String path, String signature, String time) throws IOException {
        String addPath = String.format(path, time);
        // create test case
        TestCase tc = new TestCase(name, description);
        String expectedBody = """
                "time": 1658484715035,
                        "statusCode": 200,
                        "errorCode": 0,
                        "errorDes": "",
                        "data": []""";

        // create test step 1
        String desc2 = "Verify response data of request";
        TestAction step2 = sendApi(desc2, addPath, signature, null, HttpMethod.GET, String.format(responseFormat,expectedBody), List.of("time"));

        //add step & run
        tc.addStep(step2);
        tc.run();
    }

}
