package reader;

import com.automation.test.reader.AbstractReader;

import java.net.HttpURLConnection;

public class HttpResponseCodeReader extends AbstractReader<Integer> {

    private HttpURLConnection conn;

    public HttpResponseCodeReader(HttpURLConnection conn) {
        this.conn = conn;
    }

    @Override
    public Integer read() throws Exception {
        return conn.getResponseCode();
    }

}
