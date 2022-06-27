package ExpenseManagement;

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
import reader.*;
import verifier.MultiStringContainsVerifier;
import verifier.SimpleStringContainsVerifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

public class AbstractExpenseManagementTest extends AbstractMServiceNonApp {
    public static final FileHelper file = new FileHelper();

    public final String responseFormat = "{\n" +
            "    \"user\": \"0909498114\",\n" +
            "    \"result\": true,\n" +
            "    \"errorCode\": 0,\n" +
            "    \"errorDesc\": \"\",\n" +
            "    \"data\": {%s" +
            "    }\n" +
            "}";

    final int status = 200;
    String signatureKey = "M-Signature";
    String signatureValue = "QqR+k2XzeAk/I6I8e7ebOXel08tepEru1WGOvgBsGgs=";
    String backendSvcKey = "backend-svc";
    String backendSvcValue = "expense-api-transhis";
    String url = "";

    public TestAction sendApi(String decs, String path, String signatureValue, String payload, String method, String expectedBody, List<String> ignoredKeys) throws IOException {
        // config Headers
        try {


            HttpConnectionBuilder builder = new HttpConnectionBuilder();
            builder.setToken(file.getFileContent("test-data/token"));
            String url = APIUrl.URL + path;
            HttpURLConnection connection = builder.buildRESTConnection(url, method);
            // add new headers key (tùy case
//        connection.addRequestProperty("Authorization","Bearer " + token);
            connection.addRequestProperty(signatureKey, signatureValue);
            connection.addRequestProperty(backendSvcKey, backendSvcValue);

            //tạo request
            RequestInfo requestInfo = new RequestInfo(connection, payload);
            HttpPublisher httpPublisher = new HttpPublisher();
            httpPublisher.setInput(requestInfo);

            TestAction testAction = new TestAction(decs, httpPublisher);
            // verify status code
            HttpResponseCodeReader codeReader = new HttpResponseCodeReader(connection);
            SimpleVerifier<Integer> codeVerifier = new SimpleVerifier<>();
            codeVerifier.setExpected(status);
            TestVerification<?> codeVerification = new TestVerification<>(codeReader, codeVerifier);
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
        } catch (TestException e) {
            throw new TestIOException("Fail to send request", e);
        }
    }

    public TestAction sendApiContains(String desc, String path, String signatureValue, String payload, String method, String expectedBody, List<String> ignoredKeys) throws IOException {
        // config Headers
        try {

            String url = APIUrl.URL + path;
            HttpConnectionBuilder builder = new HttpConnectionBuilder();
            builder.setToken(file.getFileContent("test-data/token"));
            HttpURLConnection connection = builder.buildRESTConnection(url, method);
            // add new headers key (tùy case
            connection.addRequestProperty(signatureKey, signatureValue);
            connection.addRequestProperty(backendSvcKey, backendSvcValue);

            //tạo request
            RequestInfo requestInfo = new RequestInfo(connection, payload);
            HttpPublisher httpPublisher = new HttpPublisher();
            httpPublisher.setInput(requestInfo);

            TestAction testAction = new TestAction(desc, httpPublisher);
            // verify status code
            HttpResponseCodeReader codeReader = new HttpResponseCodeReader(connection);
            SimpleVerifier<Integer> codeVerifier = new SimpleVerifier<>();
            codeVerifier.setExpected(status);
            TestVerification<?> codeVerification = new TestVerification<>(codeReader, codeVerifier);
            codeVerification.setVerifiableInstruction("Status code of Request:\n");
            testAction.addVerification(codeVerification);

            //verify body
            HttpResponseBodyReader bodyReader = new HttpResponseBodyReader(connection);
            //bỏ các key k cần check
            SimpleStringContainsVerifier multiStringContainsVerifier = new SimpleStringContainsVerifier();
            multiStringContainsVerifier.setExpected(expectedBody);
            TestVerification<?> testVerification = new TestVerification<>(bodyReader, multiStringContainsVerifier);
            testVerification.setVerifiableInstruction("The response is contains:\n");
            testAction.addVerification(testVerification);

            return testAction;
        } catch (TestException e) {
            throw new TestIOException("Fail to send request", e);
        }
    }

    public TestAction sendApiContains(String desc, String path, String signatureValue, String payload, String method, List<String> expectedBody, List<String> ignoredKeys) throws IOException {
        // config Headers
        try {

            String url = APIUrl.URL + path;
            HttpConnectionBuilder builder = new HttpConnectionBuilder();
            builder.setToken(file.getFileContent("test-data/token"));
            HttpURLConnection connection = builder.buildRESTConnection(url, method);
            // add new headers key (tùy case
            connection.addRequestProperty(signatureKey, signatureValue);
            connection.addRequestProperty(backendSvcKey, backendSvcValue);

            //tạo request
            RequestInfo requestInfo = new RequestInfo(connection, payload);
            HttpPublisher httpPublisher = new HttpPublisher();
            httpPublisher.setInput(requestInfo);

            TestAction testAction = new TestAction(desc, httpPublisher);
            // verify status code
            HttpResponseCodeReader codeReader = new HttpResponseCodeReader(connection);
            SimpleVerifier<Integer> codeVerifier = new SimpleVerifier<>();
            codeVerifier.setExpected(status);
            TestVerification<?> codeVerification = new TestVerification<>(codeReader, codeVerifier);
            codeVerification.setVerifiableInstruction("Status code of Request:\n");
            testAction.addVerification(codeVerification);

            //verify body
            HttpResponseBodyReader bodyReader = new HttpResponseBodyReader(connection);
            //bỏ các key k cần check
            MultiStringContainsVerifier multiStringContainsVerifier = new MultiStringContainsVerifier();
            multiStringContainsVerifier.setExpected(expectedBody);
            TestVerification<?> testVerification = new TestVerification<>(bodyReader, multiStringContainsVerifier);
            testVerification.setVerifiableInstruction("The response is contains:\n");
            testAction.addVerification(testVerification);

            return testAction;
        } catch (TestException e) {
            throw new TestIOException("Fail to send request", e);
        }
    }

    public TestAction sendApiContains(String desc, String path, String signatureValue, String token, String payload, String method, String expectedBody, List<String> ignoredKeys) throws IOException {
        // config Headers
        try {

            String url = APIUrl.URL + path;
            HttpConnectionBuilder builder = new HttpConnectionBuilder();
            builder.setToken(token);
            HttpURLConnection connection = builder.buildRESTConnection(url, method);
            // add new headers key (tùy case
            connection.addRequestProperty(signatureKey, signatureValue);
            connection.addRequestProperty(backendSvcKey, backendSvcValue);

            //tạo request
            RequestInfo requestInfo = new RequestInfo(connection, payload);
            HttpPublisher httpPublisher = new HttpPublisher();
            httpPublisher.setInput(requestInfo);

            TestAction testAction = new TestAction(desc, httpPublisher);
            // verify status code
            HttpResponseCodeReader codeReader = new HttpResponseCodeReader(connection);
            SimpleVerifier<Integer> codeVerifier = new SimpleVerifier<>();
            codeVerifier.setExpected(status);
            TestVerification<?> codeVerification = new TestVerification<>(codeReader, codeVerifier);
            codeVerification.setVerifiableInstruction("Status code of Request:\n");
            testAction.addVerification(codeVerification);

            //verify body
            HttpResponseBodyReader bodyReader = new HttpResponseBodyReader(connection);
            //bỏ các key k cần check
            SimpleStringContainsVerifier multiStringContainsVerifier = new SimpleStringContainsVerifier();
            multiStringContainsVerifier.setExpected(expectedBody);
            TestVerification<?> testVerification = new TestVerification<>(bodyReader, multiStringContainsVerifier);
            testVerification.setVerifiableInstruction("The response is contains:\n");
            testAction.addVerification(testVerification);

            return testAction;
        } catch (TestException e) {
            throw new TestIOException("Fail to send request", e);
        }
    }

    public TestAction editSettingsApi(String decs, String path, String signatureValue, String payload, String method, String expectedBody, List<String> ignoredKeys) throws IOException {
        // config Headers
        try {

            String url = APIUrl.URL + path;
            HttpConnectionBuilder builder = new HttpConnectionBuilder();
            builder.setToken(file.getFileContent("test-data/token"));
            HttpURLConnection connection = builder.buildRESTConnection(url, method);
            // add new headers key (tùy case
//        connection.addRequestProperty("Authorization","Bearer " + token);
            connection.addRequestProperty(signatureKey, signatureValue);
            connection.addRequestProperty(backendSvcKey, backendSvcValue);

            //tạo request
            RequestInfo requestInfo = new RequestInfo(connection, payload);
            HttpPublisher httpPublisher = new HttpPublisher();
            httpPublisher.setInput(requestInfo);

            TestAction testAction = new TestAction(decs, httpPublisher);
            // verify status code
            HttpResponseCodeReader codeReader = new HttpResponseCodeReader(connection);
            SimpleVerifier<Integer> codeVerifier = new SimpleVerifier<>();
            codeVerifier.setExpected(status);
            TestVerification<?> codeVerification = new TestVerification<>(codeReader, codeVerifier);
            codeVerification.setVerifiableInstruction("Status code of Request:\n");
            testAction.addVerification(codeVerification);

            //verify body
            HttpResponseBodyReader bodyReader = new HttpResponseBodyReader(connection);
            //bỏ các key k cần check
            SimpleStringContainsVerifier multiStringContainsVerifier = new SimpleStringContainsVerifier();
            multiStringContainsVerifier.setExpected(expectedBody);
            TestVerification<?> testVerification = new TestVerification<>(bodyReader, multiStringContainsVerifier);
            testAction.addVerification(testVerification);

            return testAction;
        } catch (TestException e) {
            throw new TestIOException("Fail to send request", e);
        }
    }


    public TestAction querySimpleData(String decs, String query, String expected) throws IOException {
        TestAction testAction = new TestAction(decs, null);
        try {
            GetStringSqlReader reader = new GetStringSqlReader(query);
            SimpleVerifier<String> verifier = new SimpleVerifier<>();
            verifier.setExpected(expected);
            TestVerification<?> testVerification = new TestVerification<>(reader, verifier);
            testVerification.setVerifiableInstruction(String.format("The value of Query '%s' is\n", query));
            testAction.addVerification(testVerification);
        } catch (TestException e) {
            throw new TestIOException("Fail to execute query select", e);
        }
        return testAction;

    }

    public TestAction executeCountQueryDb(String decs, String query, int expected) throws IOException {
        TestAction testAction = new TestAction(decs, null);
        try {
            CountQuerySqlReader reader = new CountQuerySqlReader(query);
            SimpleVerifier<Integer> verifier = new SimpleVerifier<>();
            verifier.setExpected(expected);
            TestVerification<?> testVerification = new TestVerification<>(reader, verifier);
            testVerification.setVerifiableInstruction(String.format("The value of Query '%s' is\n", query));
            testAction.addVerification(testVerification);
        } catch (TestException e) {
            throw new TestIOException("Fail to execute query select", e);
        }
        return testAction;

    }
}
