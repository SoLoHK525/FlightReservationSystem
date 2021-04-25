package models;

import util.Database;
import util.Debug;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Booking {
    private int id;
    private String customer_id;
    private double fare;

    public Booking(String customer_id, double fare) {
        this.customer_id = customer_id;
        this.fare = fare;
    }

    public int getId() {
        return this.id;
    }

    public String getFormattedId() {
        return "B" + (this.id < 10 ? "0" + this.id : this.id);
    }

    public int addBooking() throws SQLException {
        final String addBookingStatement = "INSERT INTO BOOKINGS (CUSTOMER_ID, FARE) VALUES (?, ?)";

        String[] returnCol = { "ID" };
        Database.FastQueryResponse res = Database.fastQueryWithReturn(addBookingStatement, returnCol, this.customer_id, this.fare);

        ResultSet rs = res.statement.getGeneratedKeys();

        if(res.affectedRows == 0) {
            throw new SQLException("NO ROWS IS INSERTED");
        }

        if(rs.next()) {
            this.id = rs.getInt(1);
        }

        res.close();

        return this.id;
    }

    public static boolean truncateTable() throws SQLException {
        final String truncateTableStatement = "TRUNCATE TABLE BOOKINGS";

        return Database.fastQuery(truncateTableStatement) == 0;
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE BOOKINGS CASCADE CONSTRAINT";

        try {
            return Database.fastQuery(dropTableStatement) == 0;
        } catch (SQLException e) {
            if(e.getMessage().contains("table or view does not exist")) {
                return true;
            }else{
                throw e;
            }
        }
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateBookingTable.sql
         */

        final String createTableStatement = "CREATE TABLE BOOKINGS (\n" +
                "    ID INT NOT NULL,\n" +
                "    CUSTOMER_ID CHAR(6) NOT NULL,\n" +
                "    FARE REAL NOT NULL,\n" +
                "    PRIMARY KEY(ID),\n" +
                "    CONSTRAINT FK_CUSTOMER_ID FOREIGN KEY(CUSTOMER_ID) REFERENCES CUSTOMERS(ID) ON DELETE CASCADE\n" +
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

    public static boolean deleteTuple(String bookingID) throws SQLException {
        try {
            int id = Integer.parseInt(bookingID.replaceAll("B", ""));

            final String deleteTupleStatement = "DELETE FROM BOOKINGS WHERE ID = ?";

            return Database.fastQuery(deleteTupleStatement, id) == 1;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean deleteTuple() throws SQLException{
            final String deleteTupleStatement = "DELETE FROM BOOKINGS WHERE ID = ?";

            return Database.fastQuery(deleteTupleStatement, this.id) == 1;
    }
}
