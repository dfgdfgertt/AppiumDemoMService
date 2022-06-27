package helper;

import object.SQLConnectionInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class SQLHelper {

    public static Connection CreateConnectionSQL(SQLConnectionInfo info) throws ClassNotFoundException, SQLException {
        Class.forName(info.forName);
        Connection mConnection = DriverManager.getConnection(info.dbUrl, info.username, info.password);
        return mConnection;
    }

    public static Long getBalance(Connection connectionInput, String mPhone) throws SQLException {
        String query = String.format("select AMOUNT from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' and  id ='10000'", mPhone);
        //Create Statement Object
        Statement stmt = connectionInput.createStatement();
        // Execute the SQL Query. Store results in ResultSet
        ResultSet rs = stmt.executeQuery(query);
        JsonHelper jsonHelper = new JsonHelper();
        JSONArray mJsonArray = jsonHelper.dataToJson(rs);
        if (mJsonArray.length() > 0) {
            return mJsonArray.getJSONObject(0).getLong("AMOUNT");
        }
        return null;
    }

    public static JSONArray executeQuery(Connection connectionInput, String query) throws SQLException {
        //Create Statement Object
        Statement stmt = connectionInput.createStatement();
        // Execute the SQL Query. Store results in ResultSet
        ResultSet rs = stmt.executeQuery(query);
        JsonHelper jsonHelper = new JsonHelper();
        JSONArray mJsonArray = jsonHelper.dataToJson(rs);
        if (mJsonArray.length() > 0) {
            return mJsonArray;
        }
        return null;
    }

    public static int executeQueryCount(Connection connectionInput, String query) throws SQLException {
        //Create Statement Object
        Statement stmt = connectionInput.createStatement();
        // Execute the SQL Query. Store results in ResultSet
        ResultSet rs = stmt.executeQuery(query);
        JSONArray mJsonArray = JsonHelper.dataToJson(rs);
        JSONObject jsonObject = mJsonArray.getJSONObject(0);
        String[] parts = query.split(" ");
        if (mJsonArray.length() > 0) {
            return Integer.parseInt(jsonObject.get(parts[1]).toString());
        }
        return 0;
    }

    public static String executeQueryGetOneString(Connection connectionInput, String query) throws SQLException {
        //Create Statement Object
        Statement stmt = connectionInput.createStatement();
        // Execute the SQL Query. Store results in ResultSet
        ResultSet rs = stmt.executeQuery(query);
        JSONArray mJsonArray = JsonHelper.dataToJson(rs);
        JSONObject jsonObject = mJsonArray.getJSONObject(0);
        String[] parts = query.split(" ");
        if (mJsonArray.length() > 0) {
            return jsonObject.get(parts[1]).toString();
        }
        return null;
    }

//    public static Connection CreateConnectionTuiThanTai() throws ClassNotFoundException, SQLException {
//        Class.forName("oracle.jdbc.driver.OracleDriver");
//        String dbUrl = "jdbc:oracle:thin:@//10.129.129.116:1521/investdev";
//        //Database Username
//        String username = "SOAP_QUERY";
//        //Database Password
//        String password = "SQondev2022";
//        Connection mConnection  = DriverManager.getConnection(dbUrl,username,password);
//        return mConnection;
//        //Load mysql jdbc driver
//    }
//
//
//    public static Connection CreateConnectionProKhoa() throws ClassNotFoundException, SQLException {
//        Class.forName("oracle.jdbc.driver.OracleDriver");
//        String dbUrl = "jdbc:oracle:thin:@//localhost:1521/umarketstd";
//        //Database Username
//        String username = "soap_admin";
//        //Database Password
//        String password = "soappwdaccess";
//        Connection mConnection  = DriverManager.getConnection(dbUrl,username,password);
//        return mConnection;
//        //Load mysql jdbc driver
//    }
//
//    public static Connection CreateConnectionDev() throws ClassNotFoundException, SQLException {
//        Class.forName("oracle.jdbc.driver.OracleDriver");
//        String dbUrl = "jdbc:oracle:thin:@//172.16.13.10:1521/umarketuat";
//        //Database Username
//        String username = "Connector";
//        //Database Password
//        String password = "1234567";
//        Connection mConnection  = DriverManager.getConnection(dbUrl,username,password);
//        return mConnection;
//        //Load mysql jdbc driver
//    }

}
