package object;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;


public class BillPay {
    public String APIUrl = object.APIUrl.BASE_URL+"/rabbitClient" ;

    public String APIBody = "";

    public void setAPIBody(String Phone,String pinInput, String serviceId,String moneySource,String amoutCost) {
        String mParam = "{\n" +
                "    \"queue\": \"ha_qu_core_v7_qctest_req\",\n" +
                "    \"data\": {\n" +
                "        \"initiator\":" + "\""+Phone + "\", \n" +
                "        \"pin\":" + "\""+pinInput + "\", \n" +
                "        \"pin_encoding\": \"plain\",\n" +
                "        \"ugaml_content_type\": \"billpay\",\n" +
                "        \"class_name\": \"com.mservice.goldengate.model.BillPayRequest\",\n" +
                "        \"extraTransData\": {\n" +
                "            \"serviceId\" :" + "\""+serviceId + "\", \n" +
                "            \"money_source\":" + moneySource +"\n" +
                "        },\n" +
                "        \"requires_webtool_confirm\": \"0\", \n" +
                "        \"td_params\": \"vc_amount,pn_amount,evc_source,evc_amount,target\",\n" +
                "        \"target\": \"billpaycommon\",\n" +
                "        \"type\": 1,\n" +
                "        \"amount\":" + amoutCost +"\n" +
                "    }\n" +
                "}\n" +
                "\n" ;
        this.APIBody = mParam;
    }

    public RequestSpecBuilder clientBuilder = new RequestSpecBuilder()
            .setContentType("application/json; charset=UTF-8");


    public boolean haveToken = false;

    public boolean isHaveToken() {
        return haveToken;
    }


    public String getAPIUrl() {
        return APIUrl;
    }

    public String getAPIBody() {
        return APIBody;
    }


    public BillPay() {

    }

    public Response CallAPI(String inputBody)
    {
        System.out.println("\n"+APIUrl);

        clientBuilder = new RequestSpecBuilder();
        clientBuilder.setBody(inputBody);
        clientBuilder.setContentType("application/json; charset=UTF-8");
        RequestSpecification requestSpec = clientBuilder.build();
        Response response = given()
                .spec(requestSpec).when().post(APIUrl);
        return response ;
    }
}
