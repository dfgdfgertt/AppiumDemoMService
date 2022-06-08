package reader;

import com.automation.test.reader.AbstractReader;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HttpResponseBodyReader extends AbstractReader<String> {

    private HttpURLConnection conn;

    public HttpResponseBodyReader(HttpURLConnection conn) {
        this.conn = conn;
    }

    @Override
    public String read() throws Exception {
//        return IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
        return IOUtils.toString(new BufferedReader(new InputStreamReader(conn.getInputStream())));
    }


}
