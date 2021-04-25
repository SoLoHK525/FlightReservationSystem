package models;

import util.Database;
import util.Debug;

import java.sql.SQLException;

public class Connection {
    int id;
    String flight_no;

    public Connection(int id, String flight_no){
        this.id = id;
        this.flight_no =  flight_no;
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateConnectionTable.sql.sql
         */
        final String createTableStatement = "CREATE TABLE CONNECTIONS (\n" +
                "     ID INT NOT NULL,\n" +
                "     FLIGHT_NO VARCHAR(8) NOT NULL,\n" +
                "     PRIMARY KEY (ID, FLIGHT_NO),\n" +
                "     FOREIGN KEY(ID) REFERENCES BOOKINGS(ID),\n" +
                "     FOREIGN KEY(FLIGHT_NO) REFERENCES FLIGHTS(FLIGHT_NO)\n" +
                ")";

        return Database.fastQuery(createTableStatement) == 0;
    }

    public static boolean createCheckSeatLimitTrigger() throws SQLException {
        /**
         * sql/CheckSeatLimitTrigger.sql
         */

        final String createTriggerStatement = "CREATE OR REPLACE TRIGGER CHECKSEATLIMIT\n" +
                "BEFORE INSERT OR UPDATE ON CONNECTIONS\n" +
                "FOR EACH ROW\n" +
                "DECLARE\n" +
                "    FLIGHT_SEAT_LIMIT INTEGER;\n" +
                "BEGIN\n" +
                "    SELECT SEAT_LIMIT INTO FLIGHT_SEAT_LIMIT FROM FLIGHTS WHERE FLIGHT_NO = :new.FLIGHT_NO;\n" +
                "\n" +
                "    IF(FLIGHT_SEAT_LIMIT <= 0) THEN\n" +
                "        RAISE_APPLICATION_ERROR(-20000, 'No Seat Left');\n" +
                "    ELSE\n" +
                "        UPDATE FLIGHTS SET SEAT_LIMIT = FLIGHT_SEAT_LIMIT - 1 WHERE FLIGHT_NO = :new.FLIGHT_NO;\n" +
                "    END IF;\n" +
                "END;";

        return Database.fastQuery(createTriggerStatement) == 0;
    }

    public static boolean createCancelBookingTrigger() throws SQLException {
        /**
         * sql/CancelBooking.sql
         */
        final String createTriggerStatement = "CREATE OR REPLACE TRIGGER CANCELBOOKING\n" +
                "AFTER DELETE ON BOOKINGS\n" +
                "FOR EACH ROW\n" +
                "BEGIN\n" +
                "    UPDATE FLIGHTS SET SEAT_LIMIT = SEAT_LIMIT + 1 WHERE FLIGHT_NO IN (SELECT FLIGHT_NO FROM CONNECTIONS WHERE ID = :old.ID);\n" +
                "    DELETE FROM CONNECTIONS WHERE ID = :old.ID;\n" +
                "END;";

        return Database.fastQuery(createTriggerStatement) == 0;
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE CONNECTIONS CASCADE CONSTRAINT";

        return Database.fastQuery(dropTableStatement) == 0;
    }

    public int addConnection() throws SQLException {
        /**
         * sql/AddConnection.sql
         */
        final String addConnectionStatement = "INSERT INTO CONNECTIONS (\n" +
                "    ID, \n" +
                "    FLIGHT_NO\n" +
                ") VALUES (?, ?)";

        return Database.fastQuery(addConnectionStatement, this.id, this.flight_no);
    }

    public static void autofill() throws SQLException {
        try {
            if (Connection.dropTable()) Debug.info("DROPPED TABLE [CONNECTIONS]");
        }catch(SQLException e) {
            Debug.info("TABLE [CONNECTIONS] DOES NOT EXIST");
        }

        if (Connection.createTable()) Debug.info("CREATED TABLE [CONNECTIONS]");

        if (Connection.createCheckSeatLimitTrigger()) Debug.info("CREATED CHECK_SEAT_LIMIT_TRIGGER FOR TABLE [CONNECTIONS]");

        if (Connection.createCancelBookingTrigger()) Debug.info("CREATED CANCEL_BOOKING_TRIGGER FOR TABLE [CONNECTIONS]");
    }
}
