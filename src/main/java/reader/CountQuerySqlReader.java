package reader;

import com.automation.test.reader.AbstractReader;
import helper.SQLHelper;
import object.SQLConnectionInfo;

import java.sql.Connection;

public class CountQuerySqlReader extends AbstractReader<Integer> {
    private String query;
    private Connection connection = SQLConnectionInfo.connection;

    private SQLHelper helper = new SQLHelper();

    public CountQuerySqlReader(String query) {
        this.query = query;
    }

    @Override
    public Integer read() throws Exception {
        return SQLHelper.executeQueryCount(query);
    }
}
