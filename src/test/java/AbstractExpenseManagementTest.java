import com.automation.test.TestAction;
import com.automation.test.TestVerification;
import com.automation.test.exception.TestIOException;
import com.automation.test.verifier.SimpleVerifier;
import helper.HttpConnectionBuilder;
import object.RequestInfo;
import org.testng.TestException;
import publisher.HttpPublisher;
import reader.HttpResponseBodyReader;
import reader.HttpResponseCodeReader;
import reader.HttpResponseHeadersReader;
import verifier.MultiStringContainsVerifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class AbstractExpenseManagementTest {
    String signatureKey = "M-Signature";
    String signatureValue = "mFjct6GkqPDWZhtEi7UFoXCnTJsADcUBchfjVAem08Q=";
    String backendSvcKey = "backend-svc";
    String backendSvcValue = "expense-api-transhis";
    String url = "https://api.mservice.com.vn/transhis/api/expense/category-p2p";
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoiMDkwOTQ5ODExNCIsInBhc3MiOiIwMDAwMDAiLCJzZXNzaW9uS2V5IjoiaDhGZWtXMzkzL3Ixc3dVN1RZd2lCbElGOE4xNDV0ZllYUE5RU3pLdjhXOTJobng3ZFhSYWhBPT0iLCJpbWVpIjoiODE4ODNkOGItYWQzZS00YTQ5LTkzOWYtMWI3MTRiOGE3MWE3IiwiQkFOS19DT0RFIjoiMTAyIiwiQkFOS19OQU1FIjoiVmlldGluQmFuayIsIk1BUF9TQUNPTV9DQVJEIjowLCJOQU1FIjoiVFJBTiBMT05HIiwiSURFTlRJRlkiOiJDT05GSVJNIiwiREVWSUNFX09TIjoiQW5kcm9pZCIsIkFQUF9WRVIiOjMxMTQwLCJhZ2VudF9pZCI6MTUxMDk4NzEsIk5FV19MT0dJTiI6dHJ1ZSwicGluIjoiL3A1VHdrcWdqUVk9IiwiaXNTaG9wIjpmYWxzZSwiaWF0IjoxNjUzOTg2OTUzfQ.DgQekTTXa267GLzw3D3r28OgRTF67fDoJOX6mIW5RnO0kL8bpe_egy6MN8_JP4edzuPV3v0e8LQEN_NfA6aYnVRm5eMwmONBXgHfBAMBD1q9RSmfract1ZSz0lZXkdnwwv3nXSyZBzHKiPSYCKM-0MOPHYLSaCYBTe_h9ZKxWt156mzOfk_D24l_J_KZ1-210JPUCtyw5mPTvXuaCproAIP5vn0MyykUwyQn4lqb5nEIPUTvS6Ia0yOuXPxeXzMimb9wW282PCyxzhGvm3jxNK4UX0V5Onl0cetNUCnVqgxzLX5cKCEUnRGyBBLQzPhRgQXLATr1ukSIdNtlNu_whA";

    public TestAction sendApi(String decs,String url, String token, String payload, String method, int expectedCode, List<String> expectedBody, List<String> expectedHeaders) throws IOException {
        // config Headers
        try {


        HttpConnectionBuilder builder = new HttpConnectionBuilder();
        builder.setToken(token);
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
        MultiStringContainsVerifier bodyVerifyer = new MultiStringContainsVerifier();
        bodyVerifyer.setExpected(expectedBody);
        TestVerification<?> bodyVerification = new TestVerification<>(bodyReader,bodyVerifyer);
        bodyVerification.setVerifiableInstruction("Body is contains:\n");
        testAction.addVerification(bodyVerification);

        //verify header
        HttpResponseHeadersReader headReader = new HttpResponseHeadersReader(connection);
        MultiStringContainsVerifier headVerifyer = new MultiStringContainsVerifier();
        headVerifyer.setExpected(expectedHeaders);
        TestVerification<?> headVerification = new TestVerification<>(headReader,headVerifyer);
        headVerification.setVerifiableInstruction("Headers is contains:\n");
        testAction.addVerification(headVerification);

        return testAction;
        }catch (TestException e){
            throw new TestIOException("Fail to send request",e);
        }
    }

}
