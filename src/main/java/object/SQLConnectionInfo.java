package object;

import helper.SQLHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnectionInfo {

    public static String forName = "oracle.jdbc.driver.OracleDriver";
    public static String password;
    public static String username;
    public static String dbUrl;

    public static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection connection) {
        SQLConnectionInfo.connection = connection;
    }


    public static void setSQLConnectionInfo(String username, String password, String dbUrl) throws SQLException {
        SQLConnectionInfo.username = username;
        SQLConnectionInfo.password = password;
        SQLConnectionInfo.dbUrl = dbUrl;
        SQLConnectionInfo.connection = DriverManager.getConnection(dbUrl, username, password);;
    }

    public static SQLConnectionInfo getSQLConnectionInfo() {
        return SQLConnectionInfo.getSQLConnectionInfo();
    }


    public String getForName() {
        return forName;
    }

    public void setForName(String forName) {
        this.forName = forName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
}
