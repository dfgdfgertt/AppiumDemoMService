import com.automation.test.TestAction;
import com.automation.test.TestCase;
import com.automation.test.TestVerification;
import com.automation.test.publisher.SetInputPublisher;
import com.automation.test.verifier.SimpleVerifier;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import reader.CashinReader;

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
        TestCase tc = new TestCase(name, Desc);
        String desc = String.format("Nạp '%s'đ",cashin);

        SetInputPublisher publisher = new SetInputPublisher();
        publisher.setInput("Input ở đây");

        TestAction testAction = new TestAction(desc,publisher);
        CashinReader reader = new CashinReader(cashin);
        reader.setInfo(info);
        SimpleVerifier verifier = new SimpleVerifier<>();
        verifier.setExpected(status);
        TestVerification<?> testVerification = new TestVerification<>(reader,verifier);
        testAction.addVerification(testVerification);

        tc.addStep(testAction);
        tc.run();
    }

//    @AfterMethod
//    public void tearDown(){
//        if (null != driver){
//            driver.quit();
//        }
//    }


}
