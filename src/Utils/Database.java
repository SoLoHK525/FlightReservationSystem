package Utils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private Session proxySession;
    private static Connection conn = null;

    private String hostname;
    private int port;
    private String database;

    public Database() {
        this.hostname = Config.databaseHost;
        this.port = Config.databasePort;
        this.database = Config.database;
    }

    public static Connection getConnection() {
        if(conn == null) {
            throw new RuntimeException("DatabaseConnection is not initialized, please run Database.connect()");
        }

        return conn;
    }

    public void connect(String username, String password) throws SQLException {
        final String URL = String.format("jdbc:oracle:thin:@%s:%d/%s", this.hostname, this.port, this.database);

        System.out.println("Logging " + URL + " ...");
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
}
