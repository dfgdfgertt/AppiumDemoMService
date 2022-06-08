package helper;


import com.automation.test.exception.TestIOException;
import constants.HttpMethod;
import object.APIUrl;
import object.UserInfo;
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
    private boolean auth = false;

    public HttpConnectionBuilder(String usename, String password){
        this.password=password;
        this.username=usename;

    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    protected UserInfo info;

    protected URL restEndpoint(String subPath) throws TestIOException, MalformedURLException {
        String url = APIUrl.BASE_URL + "/rabbitClient" ;
        return new URL(url);
    }

    public HttpURLConnection buildRESTConnection(String subPath, String method) throws TestIOException {
        HttpURLConnection conn=null;
        try {
            URL url = restEndpoint(subPath);
            conn = (HttpURLConnection) url.openConnection();
            if (!auth) {
                String auth = username+ ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
                String authHeaderValue = "Basic " + new String(encodedAuth);
                conn.addRequestProperty("Authorization", authHeaderValue);

            }
            //For JSON test
            conn.addRequestProperty("Accept", "*/*;q=0.8");

            switch (method) {
                case HttpMethod.GET:
                    conn.setRequestMethod("GET");
                    break;
                case HttpMethod.POST:
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "text/plain");

                    break;
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
