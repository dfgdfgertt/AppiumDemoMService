package helper;


import com.automation.test.exception.TestIOException;
import constants.HttpMethod;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpConnectionBuilder {
    private String password;
    private String username;
    private String token;

    public HttpConnectionBuilder(String username, String password){
        this.password=password;
        this.username=username;

    }


    public HttpConnectionBuilder(){

    }

    public void setToken(String token) {
        this.token = token;
    }



    protected URL restEndpoint(String path) throws TestIOException, MalformedURLException {
        return new URL(path);
    }

    public HttpURLConnection buildRESTConnection(String path, String method){
        HttpURLConnection conn=null;
        try {
            URL url = restEndpoint(path);
            conn = (HttpURLConnection) url.openConnection();
            String authHeaderValue = "";
            if ( username != null || password != null) {
                String auth = username+ ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
                 authHeaderValue = "Basic " + new String(encodedAuth);
                conn.addRequestProperty("Authorization", authHeaderValue);
            }else if (token != null) {
                authHeaderValue = "Bearer " + token;
            }
            //For JSON test
            conn.addRequestProperty("Accept", "*/*;q=0.8");
            conn.addRequestProperty("Authorization", authHeaderValue);



            switch (method) {
                case HttpMethod.GET:
                    conn.setRequestMethod("GET");
                    break;
                case HttpMethod.POST:
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    break;
                case HttpMethod.PUT:
                    conn.setRequestMethod("PUT");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                case HttpMethod.OPTIONS:
                    conn.setRequestMethod("OPTIONS");
                    break;
                default:
                    throw new TestIOException("Not supported method name: " + method);
            }
            return conn;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return conn;
    }
}
