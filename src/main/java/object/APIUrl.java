package object;

import helper.JsonHelper;
import org.json.JSONArray;

import java.sql.*;

public class APIUrl{
    public static final String BASE_URL = "http://172.16.13.13:1234";

    public static class SQLConnection {

        private Connection mConnectionTuiThanTai;

        private static JsonHelper jsonHelper = new JsonHelper();

        public static Connection CreateConnectionTuiThanTai() throws ClassNotFoundException, SQLException {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String dbUrl = "jdbc:oracle:thin:@//10.129.129.116:1521/investdev";
            //Database Username
            String username = "SOAP_QUERY";
            //Database Password
            String password = "SQondev2022";
            Connection mConnection  = DriverManager.getConnection(dbUrl,username,password);
            return mConnection;
            //Load mysql jdbc driver
        }


        public static Connection CreateConnectionProKhoa() throws ClassNotFoundException, SQLException {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String dbUrl = "jdbc:oracle:thin:@//localhost:1521/umarketstd";
            //Database Username
            String username = "soap_admin";
            //Database Password
            String password = "soappwdaccess";
            Connection mConnection  = DriverManager.getConnection(dbUrl,username,password);
            return mConnection;
            //Load mysql jdbc driver
        }

        public static Connection CreateConnectionDev() throws ClassNotFoundException, SQLException {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String dbUrl = "jdbc:oracle:thin:@//172.16.13.10:1521/umarketuat";
            //Database Username
            String username = "Connector";
            //Database Password
            String password = "1234567";
            Connection mConnection  = DriverManager.getConnection(dbUrl,username,password);
            return mConnection;
            //Load mysql jdbc driver
        }


        public static JSONArray DBProKhoaCheckWhiteList(Connection conectionInput, String mPhone) throws SQLException {
            String query = "select ad.agent_id,\n" +
                    "        ar.reference,\n" +
                    "        ad.AD_KEY,\n" +
                    "       ad.VALUE\n" +
                    "\n" +
                    "    from UMARKETADM.AGENT_DATA ad\n" +
                    "    left join UMARKETADM.AGENT_REF ar on ad.agent_id=ar.bodyid\n" +
                    "    where 1=1\n" +
                    "--    and ar.reference = 'shbbank.bank'\n" +
                    "    and ar.reference = '"+mPhone+"' and ad.ad_key = 'loyalty_whitelist_addpoint' \n" +
                    "    and ar.deleted = 0";
            //Create Statement Object
            Statement stmt = conectionInput.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray mJsonArray = jsonHelper.dataToJson(rs);
            if(mJsonArray.length() > 0)
            {
                return mJsonArray;
            }
            return null;
        }


        public static JSONArray DBProKhoaCheckDBSerIdCashBack(Connection conectionInput,String mId) throws SQLException {
            String query = "select ad_key,value\n" +
                    "from UMARKETADM.AGENT_DATA ad\n" +
                    "left join UMARKETADM.AGENT_REF ar on ad.agent_id=ar.bodyid\n" +
                    "where 1=1\n" +
                    "and ar.reference = 'loyalty_cashback_v3'\n" +
                    "and ad.ad_key like '%."+mId+"'\n" +
                    "and ar.deleted = 0";
            //Create Statement Object
            Statement stmt = conectionInput.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray mJsonArray = jsonHelper.dataToJson(rs);
            if(mJsonArray.length() > 0)
            {
                return mJsonArray;
            }
            return null;

        }


        public static JSONArray DBProKhoaCheckDBSerIdAddPoint(Connection conectionInput,String mId) throws SQLException {
            String query = "select group_id1,group_name,min_amount_new,'2000' as max_point_day,is_gmc,group_point,\n" +
            "    service_weight,group_weight,\n" +
                    "    rank1,rank2,rank3,rank4,group_max_trans,\n" +
                    "    max_total_point_new,max_per_trans_point_new,max_trans_new\n" +
                    "    from\n" +
                    "    (\n" +
                    "    select  lg.id as group_id1,lg.group_name as group_name,\n" +
                    "        lg.is_gmc as is_gmc,lg.group_point as group_point,\n" +
                    "        ls.service_weight as service_weight,lg.group_weight as group_weight, \n" +
                    "        lg.level1_weight as rank1,lg.level2_weight as rank2,\n" +
                    "        lg.level3_weight as rank3,lg.level4_weight as rank4,\n" +
                    "        lg.g_max_trans as group_max_trans,\n" +
                    "        case when ls.min_amount is null then lg.gs_min_amount else ls.min_amount end as min_amount_new, --s? ti?n nh? nh?t\n" +
                    "        case when ls.max_point is null then lg.gs_max_point else ls.max_point end as max_total_point_new, -- s? ?i?m t?i da nh?n ???c trong 1 tháng\n" +
                    "        case when ls.max_trans_point is null then lg.gs_max_trans_point else ls.max_trans_point end as max_per_trans_point_new, -- s? ?i?m t?i ?a nh?n ???c trên m?i giao d?ch\n" +
                    "        case when ls.max_trans is null then lg.gs_max_trans else ls.max_trans end as max_trans_new --s? l?n giao d?ch t?i ta trong 1 tháng\n" +
                    "    from umarketadm.loyalty_groups lg\n" +
                    "    INNER JOIN umarketadm.loyalty_services ls on ls.gid = lg.id\n" +
                    "    where ls.service_name = '"+mId+"'\n" +
                    ")";
            //Create Statement Object
            Statement stmt = conectionInput.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray mJsonArray = jsonHelper.dataToJson(rs);
            if(mJsonArray.length() > 0)
            {
                return mJsonArray;
            }
            return null;

        }


        public static JSONArray DBProKhoaCheckUserLevel(Connection conectionInput,String mPhone) throws SQLException {
            String query = "select *\n" +
                    "FROM  umarketadm.agent_ref ar\n" +
                    "inner JOIN umarketadm.agent_data ad on  ad.agent_id = ar.bodyid\n" +
                    "where ar.reference = '"+mPhone+"'\n" +
                    "and ad.ad_key = 'level_loyalty'\n" +
                    "and ar.deleted = 0";
            //Create Statement Object
            Statement stmt = conectionInput.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray mJsonArray = jsonHelper.dataToJson(rs);
            if(mJsonArray.length() > 0)
            {
                return mJsonArray;
            }
            return null;

        }


        public static JSONArray DBProKhoaDeletedWallet(Connection conectionInput,String mPhone) throws SQLException {
            String query = "select *\n" +
                    "from UMARKETADM.AGENT_REF ar \n" +
                    "where 1=1\n" +
                    "and ar.reference = '"+mPhone+"'";
            //Create Statement Object
            Statement stmt = conectionInput.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray mJsonArray = jsonHelper.dataToJson(rs);
            if(mJsonArray.length() > 0)
            {
                return mJsonArray;
            }
            return null;

        }

        public static JSONArray DBTTTGetRecoupAddPointData(Connection conectionInput,String idInput) throws SQLException {
            String query = "SELECT ID AS TRAN_ID,\n" +
                    "       MOMO_TID AS PARENT_TRAN_ID,\n" +
                    "       TYPE_ AS TYPE_IO,\n" +
                    "       AMOUNT AS POINT,\n" +
                    "       SERVICEID AS SERVICE_ID,   CREATED AS TRAN_TIME,\n" +
                    "       TO_CHAR(TO_DATE('1970-01-01 00','yyyy-mm-dd hh24') + (TO_NUMBER(TRAN_TIME_EXPIRE) + 25200000)/1000/60/60/24, 'YYYY-MM-DD HH24:mi:ss') AS TRAN_TIME_EXPIRE,\n" +
                    "       STATE AS STATE, LEVEL_ AS CLASS_ID\n" +
                    "FROM UMARKETADM.MS_ALL_TRANS_LOYALTY\n" +
                    "WHERE MOMO_TID = '"+idInput+"' AND TYPE_ = 'addpoint'";
            //Create Statement Object
            Statement stmt = conectionInput.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray mJsonArray = jsonHelper.dataToJson(rs);
            if(mJsonArray.length() > 0)
            {
                // While Loop to jsonArray through all data and print results
    //            while (rs.next()){
    //                String myName = rs.getString(1);
    //                String myAge = rs.getString(2);
    //                System. out.println(myName+"  "+myAge);
    //            }
                return mJsonArray;
            }
            return null;

        }


        public static JSONArray DBTTTGetRecoupCashBackData(Connection conectionInput,String idInput) throws SQLException {
            String query = "SELECT ID AS TRAN_ID,\n" +
                    "       MOMO_TID AS PARENT_TRAN_ID,\n" +
                    "       TYPE_ AS TYPE_IO,\n" +
                    "       AMOUNT AS POINT,\n" +
                    "       SERVICEID AS SERVICE_ID,   CREATED AS TRAN_TIME,\n" +
                    "       TO_CHAR(TO_DATE('1970-01-01 00','yyyy-mm-dd hh24') + (TO_NUMBER(TRAN_TIME_EXPIRE) + 25200000)/1000/60/60/24, 'YYYY-MM-DD HH24:mi:ss') AS TRAN_TIME_EXPIRE,\n" +
                    "       STATE AS STATE, LEVEL_ AS CLASS_ID\n" +
                    "FROM UMARKETADM.MS_ALL_TRANS_LOYALTY\n" +
                    "-- Type \n" +
                    "-- loyalty_cashback | recoup_loyalty_cashback | addpoint\n" +
                    "WHERE MOMO_TID = '"+idInput+"' AND TYPE_ = 'recoup_loyalty_cashback'";
            //Create Statement Object
            Statement stmt = conectionInput.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray mJsonArray = jsonHelper.dataToJson(rs);
            if(mJsonArray.length() > 0)
            {
                return mJsonArray;
            }
            return null;

        }

        public static void  main(String[] args) throws  ClassNotFoundException, SQLException {
            //Connection URL Syntax: "jdbc:mysql://ipaddress:portnumber/db_name"
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String dbUrl = "jdbc:oracle:thin:@//10.129.129.116:1521/investdev";
            //Database Username
            String username = "SOAP_QUERY";
            //Database Password
            String password = "SQondev2022";
            //Query to Execute
            String query = "select * from umarketadm.loyalty_services where GID = '6'";
            //Load mysql jdbc driver

            //Create Connection to DB
            Connection con = DriverManager.getConnection(dbUrl,username,password);
            //Create Statement Object
            Statement stmt = con.createStatement();
            // Execute the SQL Query. Store results in ResultSet
            ResultSet rs= stmt.executeQuery(query);
            JSONArray jsonArray = jsonHelper.dataToJson(rs);
            System. out.println("Tuanldv"+jsonArray.get(0).toString());
            // While Loop to jsonArray through all data and print results
            while (rs.next()){
                String myName = rs.getString(1);
                String myAge = rs.getString(2);
                System. out.println(myName+"  "+myAge);
            }
            // closing DB Connection
            con.close();
        }

    }
}
