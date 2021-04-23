package test;

import com.jcraft.jsch.JSchException;
import org.junit.jupiter.api.BeforeAll;
import util.Database;

import java.sql.SQLException;

interface IDatabaseTest {
    @BeforeAll
    static void connectToServer() throws JSchException, SQLException {
        String proxyUser = System.getenv("PROXYUSER");
        String proxyPassword = System.getenv("PROXYPASSWORD");
        String dbUser = System.getenv("DBUSER");
        String dbPassword = System.getenv("DBPASSWORD");

        if (proxyUser == null || proxyUser.isEmpty()) {
            throw new RuntimeException("PROXYUSER is empty");
        }

        if (proxyPassword == null || proxyPassword.isEmpty()) {
            throw new RuntimeException("PROXYUSER is empty");
        }

        if (dbUser == null || dbUser.isEmpty()) {
            throw new RuntimeException("DBUSER is empty");
        }

        if (dbPassword == null || dbPassword.isEmpty()) {
            throw new RuntimeException("DBPASSWORD is empty");
        }

        new Database().useProxy(proxyUser, proxyPassword).connect(dbUser, dbPassword);
    }
}
