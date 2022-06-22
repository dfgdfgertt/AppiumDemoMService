import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import helper.SQLHelper;
import object.SQLConnectionInfor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

public class ExpenseManagementApiTest extends AbstractExpenseManagementTest{

    @DataProvider(name = "apiTestData")
    public Object[][] apiTestData() {
        return new Object[][]{
                {
                        "Case 1", "GET - category p2p by config", 200,
                        "{\n" +
                                "    \"user\": \"0909498115\",\n" +
                                "    \"result\": true,\n" +
                                "    \"errorCode\": 0,\n" +
                                "    \"errorDesc\": \"\",\n" +
                                "    \"data\": {\n" +
                                "        \"time\": 1655867133674,\n" +
                                "        \"statusCode\": 200,\n" +
                                "        \"errorCode\": 0,\n" +
                                "        \"errorDes\": null,\n" +
                                "        \"expenseCategories\": []\n" +
                                "    }\n" +
                                "}",
                }
        };
    }
    @Test(dataProvider = "apiTestData")
    public void getCategoryP2p(String name, String desc,  int status, String expectedBody) throws IOException, SQLException, ClassNotFoundException {

        // create test case
        TestCase tc = new TestCase(name, desc);

        // create test step 1
        String step = "Verify response data of request";
        String path =  "category-p2p";
        TestAction testAction = sendApi(step,path,signatureValue,null, HttpMethod.GET,status, expectedBody,Collections.singletonList("time"));
        // actual

        //add step & run
        tc.addStep(testAction);
        tc.run();
    }
    @DataProvider(name = "getMoneySourceTestData")
    public Object[][] getMoneySourceTestData() {
        return new Object[][]{
                {
                        "Case 2", "GET - money source", 200,
                        "{\n" +
                                "                \"idNew\": 0,\n" +
                                "                \"moneySourceType\": null,\n" +
                                "                \"amount\": %s,\n" +
                                "                \"iconId\": 0,\n" +
                                "                \"moneySourceName\": \"Ví MoMo\",\n" +
                                "                \"groupMoneySource\": 0,\n" +
                                "                \"extras\": null,\n" +
                                "                \"id\": 10000,\n" +
                                "                \"userId\": \"0909498115\",\n" +
                                "                \"moneySourceCredit\": 0,\n" +
                                "                \"creditAvailable\": 0,\n" +
                                "                \"isDeleted\": 0,\n" +
                                "                \"iconLink\": \"https://img.mservice.com.vn/app/img/funds_manager/logo-momo.png\",\n" +
                                "                \"groupMoneySourceName\": \"\",\n" +
                                "                \"moneySourceNameEn\": null,\n" +
                                "                \"parentId\": null\n" +
                                "            },",
                }
        };
    }

    @Test(dataProvider = "getMoneySourceTestData")
    public void getMoneySource(String name, String desc,  int status, String expectedBody) throws IOException {
        // create test case
        TestCase tc = new TestCase(name, desc);

        // create test step 1
        String step = "Verify response data of request";
        String path = "money-source";
        TestAction testAction = sendApiContains(step,path,signatureValue,null, HttpMethod.GET,status, String.format(expectedBody,info.getBalance()),Collections.singletonList("time"));
        // actual

        //add step & run
        tc.addStep(testAction);
        tc.run();
    }
}
