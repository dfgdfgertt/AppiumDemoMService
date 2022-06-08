import com.automation.test.TestAction;
import com.automation.test.publisher.Publisher;
import helper.AbstractMServiceApp;
import helper.AbstractMServiceInstall;

public class AbstractMService extends AbstractMServiceApp {

    public TestAction walletBalanceCheckValue(String desc){
        TestAction testAction = new TestAction(desc,null);
        return null;
    }

    public long getWalletBalanceValue(){
        return 0L;
    }

    public TestAction TransHistoryCheck(){
        return null;
    }
}
