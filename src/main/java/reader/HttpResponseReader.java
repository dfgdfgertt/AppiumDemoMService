package reader;

import com.automation.test.reader.AbstractReader;
import com.automation.test.result.ResponseInfo;
import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class HttpResponseReader extends AbstractReader<ResponseInfo> {

    private HttpURLConnection conn;

    public HttpResponseReader(HttpURLConnection conn) {
        this.conn = conn;
    }

    @Override
    public ResponseInfo read() throws Exception {
        ResponseInfo res = new ResponseInfo();

        res.setCode(conn.getResponseCode());
        res.setHeaders(conn.getHeaderFields());

        if (conn.getDoInput()) {
            res.setBody(IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8));
        }

        return res;
    }

}
