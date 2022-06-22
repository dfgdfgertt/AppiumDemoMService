import com.automation.test.TestAction;
import com.automation.test.TestVerification;
import com.automation.test.exception.TestIOException;
import com.automation.test.helper.FileHelper;
import com.automation.test.verifier.JsonVerifier;
import com.automation.test.verifier.SimpleVerifier;
import helper.AbstractMServiceNonApp;
import helper.HttpConnectionBuilder;
import helper.SQLHelper;
import object.APIUrl;
import object.RequestInfo;
import org.testng.TestException;
import publisher.HttpPublisher;
import reader.HttpResponseBodyReader;
import reader.HttpResponseCodeReader;
import reader.HttpResponseHeadersReader;
import verifier.MultiStringContainsVerifier;
import verifier.SimpleStringContainsVerifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

public class AbstractExpenseManagementTest extends AbstractMServiceNonApp {
    public static final FileHelper file = new FileHelper();
    String signatureKey = "M-Signature";
    String signatureValue = "Kt6i5dOhp39p9T5t85An+YoSquXzOld6/eQg0DglU+E=";
    String backendSvcKey = "backend-svc";
    String backendSvcValue = "expense-api-transhis";
    String url = "";

    public TestAction sendApi(String decs,String path, String signatureValue, String payload, String method, int expectedCode, String expectedBody, List<String> ignoredKeys) throws IOException {
        // config Headers
        try {


        HttpConnectionBuilder builder = new HttpConnectionBuilder();
        builder.setToken(file.getFileContent("test-data/token"));
        String url = APIUrl.URL + path;
        HttpURLConnection connection = builder.buildRESTConnection(url,method);
        // add new headers key (tùy case
//        connection.addRequestProperty("Authorization","Bearer " + token);
        connection.addRequestProperty(signatureKey,signatureValue);
        connection.addRequestProperty(backendSvcKey,backendSvcValue);

        //tạo request
        RequestInfo requestInfo = new RequestInfo(connection,payload);
        HttpPublisher httpPublisher = new HttpPublisher();
        httpPublisher.setInput(requestInfo);

        TestAction testAction = new TestAction(decs,httpPublisher);
        // verify status code
        HttpResponseCodeReader codeReader = new HttpResponseCodeReader(connection);
        SimpleVerifier<Integer> codeVerifier = new SimpleVerifier<>();
        codeVerifier.setExpected(expectedCode);
        TestVerification<?> codeVerification = new TestVerification<>(codeReader,codeVerifier);
        codeVerification.setVerifiableInstruction("Status code of Request:\n");
        testAction.addVerification(codeVerification);

        //verify body
        HttpResponseBodyReader bodyReader = new HttpResponseBodyReader(connection);
        //bỏ các key k cần check
        JsonVerifier verifier2 = new JsonVerifier(ignoredKeys);
        verifier2.setExpected(expectedBody);
        TestVerification<?> verification2 = new TestVerification<>(bodyReader, verifier2);
        verification2.setVerifiableInstruction("Response body contains: \n");
        testAction.addVerification(verification2);

        return testAction;
        }catch (TestException e){
            throw new TestIOException("Fail to send request",e);
        }
    }

    public TestAction sendApiContains(String decs,String path, String signatureValue, String payload, String method, int expectedCode, String expectedBody, List<String> ignoredKeys) throws IOException {
        // config Headers
        try {

            String url = APIUrl.URL + path;
            HttpConnectionBuilder builder = new HttpConnectionBuilder();
            builder.setToken(file.getFileContent("test-data/token"));
            HttpURLConnection connection = builder.buildRESTConnection(url,method);
            // add new headers key (tùy case
//        connection.addRequestProperty("Authorization","Bearer " + token);
            connection.addRequestProperty(signatureKey,signatureValue);
            connection.addRequestProperty(backendSvcKey,backendSvcValue);

            //tạo request
            RequestInfo requestInfo = new RequestInfo(connection,payload);
            HttpPublisher httpPublisher = new HttpPublisher();
            httpPublisher.setInput(requestInfo);

            TestAction testAction = new TestAction(decs,httpPublisher);
            // verify status code
            HttpResponseCodeReader codeReader = new HttpResponseCodeReader(connection);
            SimpleVerifier<Integer> codeVerifier = new SimpleVerifier<>();
            codeVerifier.setExpected(expectedCode);
            TestVerification<?> codeVerification = new TestVerification<>(codeReader,codeVerifier);
            codeVerification.setVerifiableInstruction("Status code of Request:\n");
            testAction.addVerification(codeVerification);

            //verify body
            HttpResponseBodyReader bodyReader = new HttpResponseBodyReader(connection);
            //bỏ các key k cần check
            SimpleStringContainsVerifier multiStringContainsVerifier = new SimpleStringContainsVerifier();
            multiStringContainsVerifier.setExpected(expectedBody);
            TestVerification<?> testVerification = new TestVerification<>(bodyReader,multiStringContainsVerifier);
            testAction.addVerification(testVerification);

            return testAction;
        }catch (TestException e){
            throw new TestIOException("Fail to send request",e);
        }
    }

}
