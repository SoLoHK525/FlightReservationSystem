package test;

import com.jcraft.jsch.JSchException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import util.Database;
import util.Debug;

import java.sql.SQLException;

interface IDatabaseTest {
    @BeforeAll
    static void connectToServer() throws JSchException, SQLException {
        new Database();
        Debug.bootstrap();
    }

    @AfterAll
    static void close() {
        Database.getInstance().close();
    }
}
