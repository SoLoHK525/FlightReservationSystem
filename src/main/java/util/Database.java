package util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import javax.xml.transform.Result;
import java.sql.*;
import java.sql.Date;
import java.util.Properties;

public class Database {
    private Session proxySession;
    private static Connection conn = null;

    private static Database instance;

    private String hostname;
    private int port;
    private String database;

    public Database() {
        if(instance != null) {
            instance.close();
        }

        this.hostname = Config.databaseHost;
        this.port = Config.databasePort;
        this.database = Config.database;

        instance = this;
    }

    public static class Response {
        public final ResultSet resultSet;
        public final PreparedStatement statement;

        Response(ResultSet rs, PreparedStatement stm) {
            this.resultSet = rs;
            this.statement = stm;
        }

        public void close() throws SQLException {
            resultSet.close();
            statement.close();
        }
    }

    public static class FastQueryResponse {
        public final int affectedRows;
        public final PreparedStatement statement;

        FastQueryResponse(int ar, PreparedStatement stm) {
            this.affectedRows = ar;
            this.statement = stm;
        }

        public void close() throws SQLException {
            statement.close();
        }
    }

    public static Database getInstance() {
        return instance;
    }

    public static Connection getConnection() {
        if(conn == null) {
            throw new RuntimeException("DatabaseConnection is not initialized, please run Database.connect()");
        }

        return conn;
    }

    private static PreparedStatement formatSQL(String statement, String[] returnFields, Object ...args) throws SQLException {
        PreparedStatement stm;
        if(returnFields == null) {
            stm = getConnection().prepareStatement(statement);
        } else {
            stm = getConnection().prepareStatement(statement, returnFields);
        }

        for(int i = 0; i < args.length; i++) {
            Class c = args[i].getClass();

            if (String.class.equals(c)) {
                stm.setString(i + 1, (String) args[i]);
            } else if (Integer.class.equals(c)) {
                stm.setInt(i + 1, (Integer) args[i]);
            } else if (Double.class.equals(c)) {
                stm.setDouble(i + 1, (Double) args[i]);
            } else if (Date.class.equals(c)) {
                stm.setDate(i + 1, (Date) args[i]);
            } else {
                throw new SQLException("Failed to serialize object ["+ c.getName() + "] at index: " + (i + 1));
            }
        }

        return stm;
    }

    public static int fastQuery(String statement) throws SQLException {
        Statement stm = getConnection().createStatement();
        int result = stm.executeUpdate(statement);
        stm.close();

        return result;
    }

    public static int fastQuery(String statement, Object ...args) throws SQLException {
        PreparedStatement stm = formatSQL(statement, null, args);
        int result = stm.executeUpdate();
        stm.close();

        return result;
    }

    public static FastQueryResponse fastQueryWithReturn(String statement, String[] returnFields, Object ...args) throws SQLException {
        PreparedStatement stm = formatSQL(statement, returnFields, args);
        int result = stm.executeUpdate();

        return new FastQueryResponse(result, stm);
    }

    public static Response query(String statement, Object ...args) throws SQLException {
        PreparedStatement stm = formatSQL(statement, null, args);
        ResultSet rs = stm.executeQuery();

        return new Response(rs, stm);
    }

    public void connect(String username, String password) throws SQLException {
        final String URL = String.format("jdbc:oracle:thin:@%s:%d/%s", this.hostname, this.port, this.database);

        Debug.info("Logging " + URL + " ...");
        conn = DriverManager.getConnection(URL, username, password);
    }

    public Database useProxy(String username, String password) throws JSchException {
        this.proxySession = new JSch().getSession(username, Config.proxyHost, Config.proxyPort);
        this.proxySession.setPassword(password);

        // Disable ssh host public key checking
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        this.proxySession.setConfig(config);
        this.proxySession.connect();

        this.proxySession.setPortForwardingL(Config.forwardHost, 0, Config.databaseHost, Config.databasePort);

        this.port = Integer.parseInt(this.proxySession.getPortForwardingL()[0].split(":")[0]);

        this.hostname = Config.forwardHost;

        return this;
    }

    public void close() {
        try {
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(proxySession != null) {
            proxySession.disconnect();
        }
    }
}