package ExpenseManagement;

import com.automation.test.TestAction;
import com.automation.test.TestCase;
import constants.HttpMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class GetCategoryP2PTest extends AbstractExpenseManagementTest {

    @DataProvider(name = "apiTestData")
    public Object[][] apiTestData() {
        return new Object[][]{
                {
                        "Case 1", "GET - Get category p2p by config",
                        """
                        {
                            "user": "0909498114",
                            "result": true,
                            "errorCode": 0,
                            "errorDesc": "",
                            "data": {
                                "time": 1655867133674,
                                "statusCode": 200,
                                "errorCode": 0,
                                "errorDes": null,
                                "expenseCategories": []
                            }
                        }""",
                }
        };
    }

    @Test(dataProvider = "apiTestData")
    public void getCategoryP2p(String name, String desc, String expectedBody) throws IOException {
        // create test case
        TestCase tc = new TestCase(name, desc);

        // create test step 1
        String step = "Verify response data of request";
        String path = "/category-p2p";
        TestAction testAction = sendApi(step, path, signatureValue, null, HttpMethod.GET, expectedBody, List.of("expenseCategories", "time"));

        //add step & run
        tc.addStep(testAction);
        tc.run();
    }



}
