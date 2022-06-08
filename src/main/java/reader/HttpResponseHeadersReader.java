package reader;

import com.automation.test.exception.TestIOException;
import com.automation.test.reader.AbstractReader;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpResponseHeadersReader extends AbstractReader<String> {

    private HttpURLConnection conn;

    public HttpResponseHeadersReader(HttpURLConnection conn) {
        this.conn = conn;
    }

    @Override
    public String read() throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            Map<String, List<String>> map = conn.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                if(entry.getKey()==null){
                    System.out.println("Key is null");

                }else{
                    System.out.println("Key : " + entry.getKey() +
                                               " ,Value : " + entry.getValue());
                    jsonObject.put(entry.getKey()==null? (String) JSONObject.NULL :entry.getKey(), entry.getValue());
                }

            }
        }catch (Exception e){
            throw new TestIOException(String.format("Failed to get response header", e.getMessage()));
        }

        return jsonObject.toString();
    }

}
