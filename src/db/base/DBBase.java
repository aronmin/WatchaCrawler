package db.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBBase {
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String url = "jdbc:mysql://gguip7554.cafe24.com:3306/gguip7554?serverTimezone=UTC&useUniCode=yes&characterEncoding=UTF-8";
    private String user = "gguip7554";
    private String password = "p7554!potato";
    private Connection connection = null;
    private PreparedStatement statement = null;
    public Connection getConnection() {
        return connection;
    }
    public PreparedStatement getStatement() {
        return statement;
    }
    public void setStatement(PreparedStatement statement) {
        this.statement = statement;
    }
    public DBBase(){
        try {
            Class.forName(driver);
            try {
                connection = DriverManager.getConnection(url,user,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public PreparedStatement createStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}