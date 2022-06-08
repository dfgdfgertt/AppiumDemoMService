package object;

public class SQLConnectionInfor {

    public String forName = "oracle.jdbc.driver.OracleDriver";
    public String password;
    public String username;
    public String dbUrl;

    public SQLConnectionInfor(String username, String password, String dbUrl) {
        this.username = username;
        this.password = password;
        this.dbUrl = dbUrl;
    }

    public SQLConnectionInfor() {
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
