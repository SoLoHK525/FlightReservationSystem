package models;

import util.Database;
import util.Debug;

import java.sql.SQLException;

public class Booking {
    String id;
    String customer_id;
    int fare;

    public static boolean truncateTable() throws SQLException {
        final String truncateTableStatement = "TRUNCATE TABLE BOOKINGS";

        return Database.fastQuery(truncateTableStatement);
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE BOOKINGS CASCADE CONSTRAINT";

        return Database.fastQuery(dropTableStatement);
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateBookingTable.sql
         */

        final String createTableStatement = "CREATE TABLE BOOKINGS (\n" +
                "    ID CHAR(3),\n" +
                "    CUSTOMER_ID CHAR(3),\n" +
                "    FARE INT,\n" +
                "    PRIMARY KEY(ID),\n" +
                "    FOREIGN KEY(CUSTOMER_ID) REFERENCES Customer(ID)\n" +
                ")";

        return Database.fastQuery(createTableStatement);
    }

    public static void autofill() throws SQLException {
        try {
            if (Booking.dropTable()) Debug.info("DROPPED TABLE [BOOKINGS]");
        }catch(SQLException e) {
            Debug.info("TABLE [BOOKINGS] DOES NOT EXIST");
        }

        if (Booking.createTable()) Debug.info("CREATED TABLE [BOOKINGS]");

    }
}
