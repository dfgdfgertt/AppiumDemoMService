package object;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class RequestInfo {
    private HttpURLConnection connection;
    private String payload;
    private Map<String, List<String>> headers;

    public RequestInfo(HttpURLConnection connection, String payload) {
        super();
        this.connection = connection;
        this.payload = payload;
        this.headers = connection.getRequestProperties(); // Must get here cause once it's connected, we cannot get anymore
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        if (connection != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s request to %s", connection.getRequestMethod(), connection.getURL()));
            if (payload != null) {
                sb.append("\nPayload: ").append(payload);
            }

            if (headers.size() > 0) {
                sb.append("\nHeader:");
                for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                    sb.append("\n  ").append(header.getKey()).append(": ").append(String.join(", ", header.getValue()));
                }
            }
            return sb.toString();
        }
        else {
            return super.toString();
        }
    }
}
