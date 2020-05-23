package de.thedodo24.commonPackage.classes;

import lombok.Getter;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQL {

    private String host;
    private String user;
    private String password;
    private String database;
    private Integer port;

    @Getter
    public Connection conn = null;
    public boolean isConnected() { return conn != null; }


    public MySQL(String host, String user, String password, String database, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    public void createInstance() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+ host +":"+ port +"/" + database + "?autoReconnect=true&characterEncoding=latin1&useConfigs=maxPerformance&useSSL=false&user=" + user + "&password=" + password);
        } catch(SQLException e) {
            sendErrorMessage(e);
        }
    }

    public void sendErrorMessage(SQLException e) {
        e.printStackTrace();
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
        try {
            if(conn.isClosed()) {
                conn = null;
                createInstance();
            }
        } catch (SQLException ex) {
            createInstance();
        }
    }

    public void killInstance() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            sendErrorMessage(ex);
        }
    }

    public void update(String qry) {
        if(conn != null) {
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(qry);
                stmt.close();
            } catch (SQLException e) {
                sendErrorMessage(e);
            }
        }
    }

    public ResultSet getResult(String qry) {
        if(conn != null) {
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(qry);
                preparedStatement.execute();
                return preparedStatement.executeQuery();
            } catch (SQLException e) {
                sendErrorMessage(e);
            }
        }
        return null;
    }


    public Map<String, Long> getCorp(String user) {
        String qry = "select Balance,Corp from `corp_balance`,`corp_user`,`corp_member`,`corp_list` " +
                "WHERE `corp_member`.`User` = '"+user+"' AND " +
                "`corp_user`.`UID` = `corp_member`.`UID` AND " +
                "`corp_user`.`CID` = `corp_balance`.`CID` AND " +
                "`corp_user`.`CID` = `corp_list`.`CID`";
        ResultSet rs = getResult(qry);
        try {
            if(rs.next()) {
                String corpName = rs.getString("Corp");
                Long corpBalance = (long) (rs.getDouble("Balance") * 100);
                rs.close();
                Map<String, Long> returnAble = new HashMap<>();
                returnAble.put(corpName, corpBalance);
                return returnAble;
            }
        } catch (SQLException e) {
            sendErrorMessage(e);
        }
        return null;
    }

    public boolean checkCorp(String name) {
        String qry = "select * from `corp_list` where `Corp`='" + name + "'";
        ResultSet rs = getResult(qry);
        try {
            return rs.next();
        } catch (SQLException throwables) {sendErrorMessage(throwables); }
        return false;
    }

    public long getBalance(String corp) {
        String qry = "select Balance from `corp_list`,`corp_balance` where `corp_list`.`Corp`='"+corp+"' and `corp_list`.`CID`= `corp_balance`.`CID`";
        ResultSet rs = getResult(qry);
        try {
            if(rs.next()) {
                long corpBalance = (long) (rs.getDouble("Balance") * 100);
                rs.close();
                return corpBalance;
            }
        } catch(SQLException e) {
            sendErrorMessage(e);
        }
        return 0;
    }

}
