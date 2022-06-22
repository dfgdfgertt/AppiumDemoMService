package publisher;


import com.automation.test.exception.TestIOException;
import com.automation.test.publisher.AbstractPublisher;
import object.RequestInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class HttpPublisher extends AbstractPublisher<RequestInfo> {
    @Override
    public void publish() throws IOException {
        logger.info(input);

        HttpURLConnection conn = input.getConnection();

        try {
            conn.connect();

        } catch (IOException e) {
            throw new TestIOException(String.format("Could not connect to %s", conn.getURL()), e);
        }

        if (conn.getDoOutput() && input.getPayload() != null) {
//            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(),StandardCharsets.UTF_8)) {
//                writer.write(input.getPayload());
//                writer.flush();
//                int responseCode = conn.getResponseCode();
//                logger.info("Response code is: " + responseCode);
//            } catch (IOException e) {
//                throw new TestIOException("Could not access output stream of Http connection", e);
//            }
            String jsonInputString = input.getPayload();
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode = conn.getResponseCode();
            logger.info("Response code is: " + responseCode);
        }
//        conn.disconnect();
    }

}
