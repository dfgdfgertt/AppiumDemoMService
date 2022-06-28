package reader;

import com.automation.test.exception.TestIOException;
import com.automation.test.reader.AbstractReader;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HttpResponseCountElementReader extends AbstractReader<Integer> {

    private HttpURLConnection conn;
    private String key ="";

    public HttpResponseCountElementReader(HttpURLConnection conn,String key) {
        this.key = key;
        this.conn = conn;
    }

    @Override
    public Integer read() throws Exception {
        String body = IOUtils.toString(new BufferedReader(new InputStreamReader(conn.getInputStream())));
        JsonParser parser = new JsonParser();
        JsonElement expectedElem = null;
        try {
            expectedElem = parser.parse(body);
        } catch (JsonParseException var6) {
            throw new TestIOException("Json string is not valid: " + var6.getMessage(), var6);
        }
        JsonArray jsonArray  = expectedElem.getAsJsonObject().get("data").getAsJsonObject().get(key).getAsJsonArray();
        return jsonArray.size();
    }

}