package helper;

import object.SQLConnectionInfor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {


    public static Connection CreateConnectionSQL(SQLConnectionInfor infor) throws ClassNotFoundException, SQLException {
        Class.forName(infor.forName);
        Connection connection = DriverManager.getConnection(infor.dbUrl, infor.username, infor.password);
        return connection;
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
