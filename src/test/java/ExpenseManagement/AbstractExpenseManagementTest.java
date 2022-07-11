package ExpenseManagement;

import com.automation.test.TestAction;
import com.automation.test.TestVerification;
import com.automation.test.exception.TestIOException;
import com.automation.test.helper.FileHelper;
import com.automation.test.verifier.JsonVerifier;
import com.automation.test.verifier.SimpleVerifier;
import constants.HttpMethod;
import helper.AbstractMServiceNonApp;
import helper.HttpConnectionBuilder;
import helper.SQLHelper;
import object.APIUrl;
import object.RequestInfo;
import object.UserInfo;
import org.testng.TestException;
import publisher.HttpPublisher;
import reader.*;
import verifier.SimpleStringContainsVerifier;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class AbstractExpenseManagementTest extends AbstractMServiceNonApp {
    public static final FileHelper file = new FileHelper();
    public final String responseFormat = """
            {
                "user": "0909498114",
                "result": true,
                "errorCode": 0,
                "errorDesc": "",
                "data": {%s    }
            }""";

    final int status = 200;
    public String signatureKey = "M-Signature";
    public String signatureValue = "QqR+k2XzeAk/I6I8e7ebOXel08tepEru1WGOvgBsGgs=";
    public String backendSvcKey = "backend-svc";
    public String backendSvcValue = "expense-api-transhis";

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
            verification2.setVerifiableInstruction("Response body is match: \n");
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
            for (int i = 0; i < expectedBody.size(); i++) {
               String expected = expectedBody.get(i) ;
                SimpleStringContainsVerifier simpleStringContainsVerifier = new SimpleStringContainsVerifier();
                simpleStringContainsVerifier.setExpected(expected);
                TestVerification<?> testVerification = new TestVerification<>(bodyReader, simpleStringContainsVerifier);
                testAction.addVerification(testVerification);
            }

//            MultiStringContainsVerifier multiStringContainsVerifier = new MultiStringContainsVerifier();
//            multiStringContainsVerifier.setExpected(expectedBody);
//            TestVerification<?> testVerification = new TestVerification<>(bodyReader, multiStringContainsVerifier);
//            testVerification.setVerifiableInstruction("The response is contains:\n");
//            testAction.addVerification(testVerification);

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

    public TestAction countElementResponse(String desc, String path, String signatureValue, String payload, String method, String key, int expectedNumber) throws IOException {
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

            TestAction testAction = new TestAction(desc, httpPublisher);
            // verify status code
            HttpResponseCountElementReader reader = new HttpResponseCountElementReader(connection, key);
            SimpleVerifier<Integer> verifier = new SimpleVerifier<>();
            verifier.setExpected(expectedNumber);
            TestVerification<?> testVerification = new TestVerification<>(reader, verifier);
            testVerification.setVerifiableInstruction(String.format("Number of '%s' data is:\n", key));
            testAction.addVerification(testVerification);
            return testAction;
        } catch (TestException e) {
            throw new TestIOException("Fail to send request", e);
        }
    }



    public Boolean addCategory(int number, String type) throws IOException {
        String requestBody = """
                {
                    "iconId": %s,
                    "name": "%s",
                    "type" : "%s",
                    "parentId" : "0"
                }""";
        String payload = String.format(requestBody, Math.random() * 100 + 1, "Add Category for test max " + type + " is number: " + number, type);
        HttpConnectionBuilder builder = new HttpConnectionBuilder();
        builder.setToken(file.getFileContent("test-data/token"));
        String url = APIUrl.URL + "/category";
        HttpURLConnection connection = builder.buildRESTConnection(url, HttpMethod.POST);
        connection.addRequestProperty(signatureKey, signatureValue);
        connection.addRequestProperty(backendSvcKey, backendSvcValue);

        //tạo request
        RequestInfo requestInfo = new RequestInfo(connection, payload);
        HttpURLConnection conn = requestInfo.getConnection();
        try {
            conn.connect();
            if (conn.getDoOutput()) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                return conn.getResponseCode() == 200;
            }
        } catch (IOException e) {
            throw new TestIOException(String.format("Could not connect to %s", conn.getURL()), e);
        }
        return false;
    }

    public Boolean addTransaction(String type) throws IOException{
        String requestBody = """
                {
                     "expenseType": %s,
                     "expenseNote": "%s",
                     "manualAmount": %s,
                     "transCategoryMapping": "",
                     "expenseCategory": %s
                 }""";
        String query ="select ID from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_GROUP where (user_id = '%s'  OR USER_ID ='SYSTEM') AND CATEGORY_TYPE = '%s'";
        List<String> categoryList = SQLHelper.executeQueryGetListString(String.format(query, UserInfo.getPhoneNumber(),type));
        int expenseType = 0;
        if (type.equals("IN"))
            expenseType++;
        else
            expenseType--;
        String payload = String.format(requestBody,expenseType,  "Add Transaction for test get index " + type ,(int)(Math.random() * 100)*1000,categoryList.get((int) (Math.random()*categoryList.size())));
        HttpConnectionBuilder builder = new HttpConnectionBuilder();
        builder.setToken(file.getFileContent("test-data/token"));
        String url = APIUrl.URL + "/transaction";
        HttpURLConnection connection = builder.buildRESTConnection(url, HttpMethod.POST);
        connection.addRequestProperty(signatureKey, signatureValue);
        connection.addRequestProperty(backendSvcKey, backendSvcValue);

        //tạo request
        RequestInfo requestInfo = new RequestInfo(connection, payload);
        HttpURLConnection conn = requestInfo.getConnection();
        try {
            conn.connect();
            if (conn.getDoOutput()) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                return conn.getResponseCode() == 200;
            }
        } catch (IOException e) {
            throw new TestIOException(String.format("Could not connect to %s", conn.getURL()), e);
        }
        return false;
    }

    public String randomDate(){
        long beginTime = Timestamp.valueOf("2022-01-01 00:00:00").getTime();
        long endTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
        long diff = endTime - beginTime + 1;
        long randomTime = beginTime + (long) (Math.random() * diff);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date randomDate = new Date(randomTime);
        return dateFormat.format(randomDate);
    }

    public String randomAmount(){
        return String.valueOf((int)(Math.random() * 100+1)*1000);
    }
}
