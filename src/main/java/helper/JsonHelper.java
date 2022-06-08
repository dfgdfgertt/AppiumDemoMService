package helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JsonHelper {

    public static JSONArray dataToJson(ResultSet rs)
    {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = null;
        try {
            rsmd = rs.getMetaData();
            while(rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i=1; i<=numColumns; i++) {
                    String column_name = rsmd.getColumnName(i);
                    obj.put(column_name, rs.getObject(column_name));
                }
                json.put(obj);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
