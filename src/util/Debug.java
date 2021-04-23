package util;

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
        } catch (SQLException | DateTime.InvalidDateException e) {
            e.printStackTrace();
        }
    }
}
