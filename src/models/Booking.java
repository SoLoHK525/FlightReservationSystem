package models;

import util.Database;
import util.Debug;

import java.awt.print.Book;
import java.sql.SQLException;

public class Booking {
    String id;
    String customer_id;
    int fare;

    public Booking(String customer_id, int fare) {
        this.customer_id = customer_id;
        this.fare = fare;
    }

    public int addBooking() throws SQLException {
        final String addBookingStatement = "";

        return Database.fastQuery(addBookingStatement);
    }

    public static boolean truncateTable() throws SQLException {
        final String truncateTableStatement = "TRUNCATE TABLE BOOKINGS";

        return Database.fastQuery(truncateTableStatement) == 0;
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE BOOKINGS CASCADE CONSTRAINT";

        return Database.fastQuery(dropTableStatement) == 0;
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateBookingTable.sql
         */

        final String createTableStatement = "CREATE TABLE BOOKINGS (\n" +
                "    ID INT,\n" +
                "    CUSTOMER_ID CHAR(6),\n" +
                "    FARE INT,\n" +
                "    PRIMARY KEY(ID),\n" +
                "    CONSTRAINT FK_CUSTOMER_ID FOREIGN KEY(CUSTOMER_ID) REFERENCES CUSTOMERS(ID)\n" +
                ")";


        return Database.fastQuery(createTableStatement) == 0;
    }

    public static boolean createTrigger() throws SQLException {
        final String triggerStatement = "CREATE OR REPLACE TRIGGER BOOKING_ID_INC\n" +
                "BEFORE INSERT ON BOOKINGS\n" +
                "FOR EACH ROW\n" +
                "BEGIN\n" +
                "    SELECT (COUNT(*) + 1) INTO :new.ID FROM BOOKINGS;\n" +
                "END;";

        return Database.fastQuery(triggerStatement) == 0;
    }

    public static void autofill() throws SQLException {
        try {
            if (Booking.dropTable()) Debug.info("DROPPED TABLE [BOOKINGS]");
        }catch(SQLException e) {
            Debug.info("TABLE [BOOKINGS] DOES NOT EXIST");
        }

        if (Booking.createTable()) Debug.info("CREATED TABLE [BOOKINGS]");

        if(Booking.createTrigger()) Debug.info("CREATED TRIGGER FOR TABLE [BOOKINGS]");
    }
}
