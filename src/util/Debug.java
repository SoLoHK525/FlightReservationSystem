package util;

import com.jcraft.jsch.JSchException;
import models.Booking;
import models.Connection;
import models.Customer;
import models.Flight;

import java.sql.Date;
import java.sql.SQLException;

public class Debug {
    public static void info(String message) {
        if (Config.DEBUG)
            System.out.println("[INFO] " + message);
    }

    public static void info(String format, Object... args) {
        if (Config.DEBUG)
            System.out.printf("[INFO] " + format, args);
    }

    public static void autoFill() {
        try {
            Flight.autofill();
            Customer.autofill();
            Booking.autofill();
            Connection.autofill();
        } catch (SQLException | DateTime.InvalidDateException e) {
            e.printStackTrace();
        }
    }

    public static void bootstrap() throws JSchException, SQLException {
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

        Database.getInstance().useProxy(proxyUser, proxyPassword).connect(dbUser, dbPassword);
    }
}
