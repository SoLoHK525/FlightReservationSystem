package models;

import util.Database;
import util.DateTime;
import util.Debug;

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
        final String addFlightStatement = "INSERT INTO FLIGHTS (\n" +
                "    FLIGHT_NO,\n" +
                "    DEPART_TIME,\n" +
                "    ARRIVE_TIME,\n" +
                "    FARE,\n" +
                "    SEAT_LIMIT,\n" +
                "    SOURCE,\n" +
                "    DEST\n" +
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
        final String truncateTableStatement = "TRUNCATE TABLE FLIGHTS";

        return Database.fastQuery(truncateTableStatement);
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE FLIGHTS CASCADE CONSTRAINT";

        return Database.fastQuery(dropTableStatement);
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateFlightTable.sql
         */

        final String createTableStatement = "CREATE TABLE FLIGHTS (\n" +
                "    FLIGHT_NO CHAR(8),\n" +
                "    DEPART_TIME DATE,\n" +
                "    ARRIVE_TIME DATE,\n" +
                "    FARE INT,\n" +
                "    SEAT_LIMIT INT,\n" +
                "    SOURCE VARCHAR(32),\n" +
                "    DEST VARCHAR(32),\n" +
                "    PRIMARY KEY(FLIGHT_NO)\n" +
                ")";

        return Database.fastQuery(createTableStatement);
    }

    public static void autofill() throws DateTime.InvalidDateException, SQLException {
        try {
            if (Flight.dropTable()) Debug.info("DROPPED TABLE [FLIGHTS]");
        }catch(SQLException e) {
            Debug.info("TABLE [FLIGHTS] DOES NOT EXIST");
        }

        if (Flight.createTable()) Debug.info("CREATED TABLE [FLIGHTS]");

        Date[] departureDates = new Date[]{
                DateTime.getDate(2015, 3, 15, 12, 0, 0),
                DateTime.getDate(2015, 3, 15, 18, 30, 0),
                DateTime.getDate(2015, 3, 15, 10, 0, 0),
                DateTime.getDate(2015, 3, 15, 15, 0, 0),
                DateTime.getDate(2015, 3, 15, 10, 0, 0),
                DateTime.getDate(2015, 3, 15, 4, 0, 0),
                DateTime.getDate(2015, 3, 15, 23, 40, 0),
                DateTime.getDate(2015, 3, 15, 8, 0, 0),
        };

        Date[] arrivalDates = new Date[]{
                DateTime.getDate(2015, 3, 15, 16, 0, 0),
                DateTime.getDate(2015, 3, 15, 23, 30, 0),
                DateTime.getDate(2015, 3, 15, 13, 0, 0),
                DateTime.getDate(2015, 3, 15, 18, 0, 0),
                DateTime.getDate(2015, 3, 15, 14, 0, 0),
                DateTime.getDate(2015, 3, 15, 9, 0, 0),
                DateTime.getDate(2015, 3, 16, 3, 0, 0),
                DateTime.getDate(2015, 3, 15, 11, 0, 0),
        };

        Flight[] flights = new Flight[]{
                new Flight("CX100", "HK", "Tokyo", departureDates[0], arrivalDates[0], 2000, 3),
                new Flight("CX101", "Tokyo", "New York", departureDates[1], arrivalDates[1], 4000, 3),
                new Flight("CX102", "HK", "Beijing", departureDates[2], arrivalDates[2], 2000, 1),
                new Flight("CX103", "Beijing", "Tokyo", departureDates[3], arrivalDates[3], 1500, 3),
                new Flight("CX104", "New York", "Beijing", departureDates[4], arrivalDates[4], 1500, 3),
                new Flight("CX105", "HK", "New York", departureDates[5], arrivalDates[5], 1000, 2),
                new Flight("CX106", "New York", "LA", departureDates[6], arrivalDates[6], 5000, 3),
                new Flight("CX107", "Beijing", "Tokyo", departureDates[7], arrivalDates[7], 1500, 3),
        };

        int addedFlight = 0;
        for (Flight flight : flights) {
            if (flight.addFlight()) ++addedFlight;
        }

        Debug.info("Added %d flights\n", addedFlight);
    }
}
