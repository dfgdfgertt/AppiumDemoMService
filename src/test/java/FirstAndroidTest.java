import com.automation.test.TestAction;
import com.automation.test.TestCase;
import com.automation.test.TestVerification;
import com.automation.test.publisher.SetInputPublisher;
import com.automation.test.verifier.SimpleVerifier;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import reader.CashinReader;
import reader.GetBalanceApiReader;

public class FirstAndroidTest  extends AbstractMService{

    @DataProvider(name = "cashinTestData")
    public Object[][] createConnectionsTestData() {
        return new Object[][]{
                {
                        "Case 1",
                        100000L,
                        "Nạp tiền thành công",
                        "Giao dịch thành công"
                }
        };
    }

    @Test(dataProvider = "cashinTestData")
    public void enterForm(String name, Long cashin , String Desc , String status){
        // create test case
        TestCase tc = new TestCase(name, Desc);
        String desc = String.format("Nạp '%s'đ",cashin);

        SetInputPublisher publisher = new SetInputPublisher();
        publisher.setInput("Input ở đây");

        // create test step 1
        TestAction testAction = new TestAction(desc,publisher);
        // actual
        CashinReader reader = new CashinReader(cashin);
        reader.setInfo(info);
        // expected
        SimpleVerifier verifier = new SimpleVerifier<>();
        verifier.setExpected(status);
        // check actual & expected
        TestVerification<?> testVerification = new TestVerification<>(reader,verifier);
        testAction.addVerification(testVerification);

        // create test step 2
        TestAction testAction1 = new TestAction("Kiểm tra tiền trong ví",null);
        // actual
        GetBalanceApiReader reader1 = new GetBalanceApiReader(info);
        // expected
        SimpleVerifier<Long> verifier1 = new SimpleVerifier<>();
        verifier1.setExpected(56300000+cashin);
        // check actual & expected
        TestVerification<?> testVerification1 = new TestVerification<>(reader1,verifier1);
        testAction1.addVerification(testVerification1);


        //add step & run
        tc.addStep(testAction);
        tc.addStep(testAction1);
        tc.run();
    }

//    @AfterMethod
//    public void tearDown(){
//        if (null != driver){
//            driver.quit();
//        }
//    }


}
