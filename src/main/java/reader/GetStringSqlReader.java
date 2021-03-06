package reader;

import com.automation.test.reader.AbstractReader;
import helper.SQLHelper;
import object.SQLConnectionInfo;
import object.UserInfo;

import java.sql.Connection;

public class GetStringSqlReader extends AbstractReader<String> {
    private String query;

    public GetStringSqlReader(String query) {
        this.query = query;
    }


    @Override
    public String read() throws Exception {
        return SQLHelper.executeQueryGetOneString(query);
    }
}
