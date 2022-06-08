package reader;

import com.automation.test.reader.AbstractReader;
import com.google.gson.JsonArray;
import com.sun.jdi.LongValue;
import helper.JsonHelper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import object.UserInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.TestException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import static io.restassured.RestAssured.given;

public class GetBalanceApiReader extends AbstractReader<Long> {

    private String APIUrl = object.APIUrl.BASE_URL + "/rabbitClient" ;
    private String APIBody = "";
    private UserInfo info;



    public GetBalanceApiReader(UserInfo info) {
        this.info = info;
    }

    public String setAPIBody(String phone) {
        String mParam = "{\n" +
                "  \"queue\": \"ha_qu_core_v7_test_req\",\n" +
                "  \"data\": {\n" +
                "    \"initiator\": \""+ phone+"\",\n" +
                "    \"pin\":\"000000\",\n" +
                "    \"pin_encoding\":\"plain\",\n" +
                "    \"ugaml_content_type\": \"balance\",\n" +
                "    \"sm_truong_test\" : 1\n" +
                "  }\n" +
                "}" ;
        this.APIBody = mParam;
        return mParam;
    }

    private RequestSpecBuilder clientBuilder = new RequestSpecBuilder()
            .setContentType("application/json; charset=UTF-8");


    private boolean haveToken = false;

    public boolean isHaveToken() {
        return haveToken;
    }


    @Override
    public Long read() throws Exception {
        try {
            clientBuilder = new RequestSpecBuilder();
            clientBuilder.setBody(setAPIBody(info.phoneNumber));
            clientBuilder.setContentType("application/json; charset=UTF-8");
            RequestSpecification requestSpec = clientBuilder.build();
            Response response = given()
                    .spec(requestSpec).when().post(APIUrl);

            JSONObject dataBillPay = new JSONObject(response.body().asString());
            JSONArray jsonArray = dataBillPay.getJSONArray("balanceObjects");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).get("walletType").equals(1)) {
                    return Long.valueOf(jsonArray.getJSONObject(i).get("balance").toString());
                }
            }
        }catch (TestException e){
            throw new TestException("Fail to call api get balance", e);
        }
        return null;
    }
}
