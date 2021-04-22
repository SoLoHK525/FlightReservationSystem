package models;

import util.Database;

import java.sql.Date;
import java.sql.SQLException;

public class Flight {
    String flightNumber;
    String departure;
    String destination;

    Date departureTime;
    Date arrivalTime;

    int fare;
    int seatLimit;

    public Flight(String flightNumber, String departure, String destination, Date departureTime, Date arrivalTime, int fare, int seatLimit) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.seatLimit = seatLimit;
    }

    public boolean addFlight() throws SQLException {
        /**
         * sql/AddFlight.sql
         */
        final String addFlightStatement = "INSERT INTO FLIGHT (\n" +
                "    Flight_No,\n" +
                "    Depart,\n" +
                "    Arrive,\n" +
                "    Fare,\n" +
                "    Seat_Limit,\n" +
                "    Source,\n" +
                "    Dest\n" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        return Database.fastQuery(addFlightStatement,
                this.flightNumber,
                this.departureTime,
                this.arrivalTime,
                this.fare,
                this.seatLimit,
                this.departure,
                this.destination
        );
    }

    public static boolean truncateTable() throws SQLException {
        final String truncateTableStatement = "TRUNCATE TABLE FLIGHT";

        return Database.fastQuery(truncateTableStatement);
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE FLIGHT";

        return Database.fastQuery(dropTableStatement);
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateFlightTable.sql
         */

        final String createTableStatement = "CREATE TABLE FLIGHT (\n" +
                "    Flight_No CHAR(8),\n" +
                "    Depart DATE,\n" +
                "    Arrive DATE,\n" +
                "    Fare INT,\n" +
                "    Seat_Limit INT,\n" +
                "    Source VARCHAR(32),\n" +
                "    Dest VARCHAR(32)\n" +
                ")";

        return Database.fastQuery(createTableStatement);
    }
}
