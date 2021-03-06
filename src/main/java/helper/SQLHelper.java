package helper;

import object.SQLConnectionInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLHelper {

    public static Statement stmt = SQLHelper.CreateConnectionSQL();


    public static Statement CreateConnectionSQL() {
        try {
            Class.forName(SQLConnectionInfo.forName);
            return DriverManager.getConnection(SQLConnectionInfo.dbUrl, SQLConnectionInfo.username, SQLConnectionInfo.password).createStatement();
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect SQL DB0", e);
        }
    }

    public static Long getBalance(String mPhone) {
        String query = String.format("select AMOUNT from SOAP_ADMIN.EXPENSE_MANAGEMENT_V2_MONEY_SOURCE where user_id = '%s' and  id ='10000'", mPhone);
        ResultSet rs;
        try {
            rs = stmt.executeQuery(query);
            JSONArray mJsonArray = JsonHelper.dataToJson(rs);
            return mJsonArray.getJSONObject(0).getLong("AMOUNT");
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Fail to execute query '%s'", query), e);

        }
    }

    public static JSONArray executeQuery(String query) {
        //Create Statement Object
        // Execute the SQL Query. Store results in ResultSet
        ResultSet rs;
        try {
            rs = stmt.executeQuery(query);
            return JsonHelper.dataToJson(rs);
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Fail to execute query '%s'", query), e);
        }
    }

    public static int executeQueryCount(String query) {
        //Create Statement Object
        ResultSet rs;
        try {
            rs = stmt.executeQuery(query);
            JSONArray mJsonArray = JsonHelper.dataToJson(rs);
            JSONObject jsonObject = mJsonArray.getJSONObject(0);
            String[] parts = query.split(" ");
            return Integer.parseInt(jsonObject.get(parts[1]).toString());
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Fail to execute query '%s'", query), e);
        }

        // Execute the SQL Query. Store results in ResultSet
    }

    public static String executeQueryGetOneString(String query) {
        //Create Statement Object
        ResultSet rs;
        try {
            rs = stmt.executeQuery(query);
            JSONArray mJsonArray = JsonHelper.dataToJson(rs);
            JSONObject jsonObject = mJsonArray.getJSONObject(0);
            String[] parts = query.split(" ");
            if (parts[1].contains(".")) {
                String[] results =parts[1].split("\\.");
                return jsonObject.get(results[1]).toString();
            }
            return jsonObject.get(parts[1]).toString();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Fail to execute query '%s'", query), e);
        }
    }

    public static List<String> executeQueryGetListString(String query) {
        //Create Statement Object
        // Execute the SQL Query. Store results in ResultSet

        try {
            ResultSet rs = stmt.executeQuery(query);
            JSONArray mJsonArray = JsonHelper.dataToJson(rs);
            String[] parts = query.split(" ");
            List<String> result = new ArrayList<>();
            for (int i = 0; i < mJsonArray.length(); i++) {
                result.add(mJsonArray.getJSONObject(i).get(parts[1]).toString());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Fail to execute query '%s'", query), e);
        }
    }

    public static List<Integer> executeQueryGetListInt(String query) {
        //Create Statement Object
        // Execute the SQL Query. Store results in ResultSet

        try {
            ResultSet rs = stmt.executeQuery(query);
            JSONArray mJsonArray = JsonHelper.dataToJson(rs);
            String[] parts = query.split(" ");
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < mJsonArray.length(); i++) {
                result.add(Integer.valueOf(mJsonArray.getJSONObject(i).get(parts[1]).toString()));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Fail to execute query '%s'", query), e);
        }
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
