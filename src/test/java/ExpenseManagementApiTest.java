import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;

public class ExpenseManagementApiTest extends AbstractExpenseManagementTest{

    @DataProvider(name = "apiTestData")
    public Object[][] createConnectionsTestData() {
        return new Object[][]{
                {
                        "Case 1",
                        "Verify response data of Get category p2p",
                        HttpMethod.GET,
                        200,
                        "{ \"user\": \"0909498114\", \"result\": true, \"errorCode\": 0, \"errorDesc\": \"\", \"data\": { \"time\": 1654743037790, \"statusCode\": 200, \"errorCode\": 0, \"errorDes\": null, \"expenseCategories\": [ { \"id\": 1, \"groupName\": \"OUT\", \"categoryType\": null, \"userId\": null, \"iconId\": 0, \"parentId\": 0, \"levelGroup\": 0, \"iconLink\": null }, { \"id\": 10, \"groupName\": \"OUT\", \"categoryType\": null, \"userId\": null, \"iconId\": 0, \"parentId\": 0, \"levelGroup\": 0, \"iconLink\": null } ] } }",
                        "application/json"
                }
        };
    }
    @Test(dataProvider = "apiTestData")
    public void enterForm(String name, String desc,  String method , int status, String expectedBody, String expectedHeaders) throws IOException {
        // create test case
        TestCase tc = new TestCase(name, desc);

        // create test step 1
        String step = "Send Http request with GET method";

        TestAction testAction = sendApi(step,url,token,"",method,status, Collections.singletonList(expectedBody), Collections.singletonList(expectedHeaders));
        // actual

        //add step & run
        tc.addStep(testAction);
        tc.run();
    }
}
