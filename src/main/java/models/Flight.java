package models;

import util.Database;
import util.DateTime;
import util.Debug;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

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

    public static ArrayList<String> getAllFlightsInfo() throws SQLException {
        Database.Response res = Database.query("SELECT FLIGHT_NO, DEPART_TIME, ARRIVE_TIME, FARE, SEAT_LIMIT, SOURCE, DEST FROM FLIGHTS");

        ArrayList<String> arr = new ArrayList<>();

        while (res.resultSet.next()) {
            String flight_no = res.resultSet.getString(1);
            String depart_time = DateTime.formatDate(res.resultSet.getDate(2));
            String arrive_time = DateTime.formatDate(res.resultSet.getDate(3));
            int fare = res.resultSet.getInt(4);
            int seat_limit = res.resultSet.getInt(5);
            String source = res.resultSet.getString(6);
            String dest = res.resultSet.getString(7);

            arr.add(String.format("Flight_no: %s\n" +
                            "Depart_Time: %s\n" +
                            "Arrive_Time %s\n" +
                            "Fare: %d\n" +
                            "Seat Limit: %d\n" +
                            "Source: %s\n" +
                            "Dest: %s",
                    flight_no, depart_time, arrive_time, fare, seat_limit, source, dest
            ));
        }

        res.close();
        return arr;
    }

    public static String getFlightInfo(String flightNumber) throws SQLException {
        Database.Response res = Database.query("SELECT FLIGHT_NO, DEPART_TIME, ARRIVE_TIME, FARE, SEAT_LIMIT, SOURCE, DEST FROM FLIGHTS WHERE FLIGHT_NO = ?", flightNumber);

        while (res.resultSet.next()) {
            String flight_no = res.resultSet.getString(1);
            String depart_time = DateTime.formatDate(res.resultSet.getDate(2));
            String arrive_time = DateTime.formatDate(res.resultSet.getDate(3));
            int fare = res.resultSet.getInt(4);
            int seat_limit = res.resultSet.getInt(5);
            String source = res.resultSet.getString(6);
            String dest = res.resultSet.getString(7);

            return String.format("Flight_no: %s\n" +
                            "Depart_Time: %s\n" +
                            "Arrive_Time %s\n" +
                            "Fare: %d\n" +
                            "Seat Limit: %d\n" +
                            "Source: %s\n" +
                            "Dest: %s",
                    flight_no, depart_time, arrive_time, fare, seat_limit, source, dest
            );
        }

        res.close();

        return "";
    }

    public static ArrayList<String> searchFlight(String departure, String destination, int stop, int hour) throws SQLException {
        ArrayList<String> arr = new ArrayList<>();

        if (stop == 1) {

            arr = searchAtNoStop(departure, destination, hour);
        } else if (stop == 2) {
            arr = searchAtNoStop(departure, destination, hour);
            arr.addAll(searchAtOneStop(departure, destination, hour));
        } else if (stop == 3) {
            arr = searchAtNoStop(departure, destination, hour);
            arr.addAll(searchAtOneStop(departure, destination, hour));
            arr.addAll(searchAtTwoStop(departure, destination, hour));
        }
        return arr;
    }

    private static ArrayList<String> searchAtNoStop(String departure, String destination, int hour) throws SQLException {
        Database.Response res = Database.query("SELECT FLIGHT_NO, FARE\n" +
                "    FROM FLIGHTS\n" +
                "    WHERE SOURCE = ? AND DEST = ?\n" +
                "    AND (ARRIVE_TIME - DEPART_TIME) * 24 <= ?\n", departure, destination, hour);
        ArrayList<String> arr = new ArrayList<>();

        while (res.resultSet.next()) {
            String flight_no = res.resultSet.getString(1);
            int fare = res.resultSet.getInt(2);
            arr.add(String.format("%s, fare: %d", flight_no, fare));
        }
        res.close();
        return arr;
    }

    private static ArrayList<String> searchAtOneStop(String departure, String destination, int hour) throws SQLException {
        Database.Response res = Database.query("SELECT F1.FLIGHT_NO, F2.FLIGHT_NO, F1.FARE+F2.FARE\n" +
                "FROM FLIGHTS F1, FLIGHTS F2\n" +
                "WHERE (F1.SOURCE = ? AND F2.DEST = ?)\n" +
                "  AND (F1.DEST = F2.SOURCE AND F1.ARRIVE_TIME <= F2.DEPART_TIME)\n" +
                "  AND (F2.ARRIVE_TIME - F1.DEPART_TIME) * 24 <= ?", departure, destination, hour);

        ArrayList<String> arr = new ArrayList<>();
        while (res.resultSet.next()) {
            String flight_no1 = res.resultSet.getString(1);
            String flight_no2 = res.resultSet.getString(2);
            int fare = res.resultSet.getInt(3);
            arr.add(String.format
                    ("%s->%s, fare: %d", flight_no1, flight_no2, (int)(fare * 0.9)));
        }
        res.close();
        return arr;
    }

    private static ArrayList<String> searchAtTwoStop(String departure, String destination, int hour) throws SQLException {
        Database.Response res = Database.query("SELECT F1.FLIGHT_NO, F2.FLIGHT_NO, F3.FLIGHT_NO, F1.FARE+F2.FARE+F3.FARE\n" +
                "FROM FLIGHTS F1, FLIGHTS F2, FLIGHTS F3\n" +
                "WHERE (F1.SOURCE = ? AND F3.DEST = ?)\n" +
                "  AND(F1.DEST = F2.SOURCE AND F1.ARRIVE_TIME <= F2.DEPART_TIME)\n" +
                "  AND(F2.DEST = F3.SOURCE AND F2.ARRIVE_TIME <= F3.DEPART_TIME)\n" +
                "  AND(F3.ARRIVE_TIME - F1.DEPART_TIME) * 24 <= ?", departure, destination, hour);
        ArrayList<String> arr = new ArrayList<>();
        while (res.resultSet.next()) {
            String flight_no1 = res.resultSet.getString(1);
            String flight_no2 = res.resultSet.getString(2);
            String flight_no3 = res.resultSet.getString(3);
            int fare = res.resultSet.getInt(4);
            arr.add(String.format
                    ("%s->%s->%s, fare: %d", flight_no1, flight_no2, flight_no3, (int)(fare * 0.75)));
        }

        res.close();
        return arr;
    }


    public static int getFare(String flightNumber) throws SQLException {
        String FareQueryStatement = "SELECT FARE FROM FLIGHTS WHERE FLIGHT_NO = ?";

        Database.Response res = Database.query(FareQueryStatement, flightNumber);

        if (res.resultSet.next()) {
            int value = res.resultSet.getInt(1);
            res.close();
            return value;
        }

        res.close();
        throw new SQLException("FLIGHT_DOES_NOT_EXISTS");
    }

    public static int deleteFlight(String flightNumber) throws SQLException {
        final String deleteFlightStatement = "DELETE FROM FLIGHTS WHERE FLIGHT_NO LIKE ?";

        return Database.fastQuery(deleteFlightStatement, flightNumber);
    }

    public static boolean truncateTable() throws SQLException {
        final String truncateTableStatement = "TRUNCATE TABLE FLIGHTS";

        return Database.fastQuery(truncateTableStatement) == 0;
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE FLIGHTS CASCADE CONSTRAINT";

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

    public static boolean createTrigger() throws SQLException {
        /**
         * sql/DeleteFlightTrigger.sql
         */
        final String createTableTrigger = "CREATE OR REPLACE TRIGGER DELETE_FLIGHT_TRIGGER\n" +
                "BEFORE DELETE ON FLIGHTS\n" +
                "FOR EACH ROW\n" +
                "DECLARE\n" +
                "COUNTS INTEGER;\n" +
                "BEGIN\n" +
                "    SELECT COUNT(*) INTO COUNTS FROM CONNECTIONS WHERE FLIGHT_NO = :OLD.FLIGHT_NO;\n" +
                "\n" +
                "    IF(COUNTS > 0) THEN\n" +
                "        RAISE_APPLICATION_ERROR(-20000, 'FLIGHT_HAS_CONNECTIONS');\n" +
                "    END IF;\n" +
                "END;";

        return Database.fastQuery(createTableTrigger) == 0;
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateFlightTable.sql
         */

        final String createTableStatement = "CREATE TABLE FLIGHTS (\n" +
                "    FLIGHT_NO VARCHAR(8) NOT NULL,\n" +
                "    DEPART_TIME DATE NOT NULL,\n" +
                "    ARRIVE_TIME DATE NOT NULL,\n" +
                "    FARE INT NOT NULL,\n" +
                "    SEAT_LIMIT INT NOT NULL,\n" +
                "    SOURCE VARCHAR(32) NOT NULL,\n" +
                "    DEST VARCHAR(32) NOT NULL,\n" +
                "    PRIMARY KEY(FLIGHT_NO)\n" +
                ")";

        return Database.fastQuery(createTableStatement) == 0;
    }

    public static void autofill() throws DateTime.InvalidDateException, SQLException {
        try {
            if (Flight.dropTable()) Debug.info("DROPPED TABLE [FLIGHTS]");
        } catch (SQLException e) {
            Debug.info("TABLE [FLIGHTS] DOES NOT EXIST");
        }

        if (Flight.createTable()) Debug.info("CREATED TABLE [FLIGHTS]");
        if (Flight.createTrigger()) Debug.info("CREATED TRIGGER [FLIGHTS]");

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
        try {
            return Database.fastQuery(addFlightStatement,
                    this.flightNumber,
                    this.departureTime,
                    this.arrivalTime,
                    this.fare,
                    this.seatLimit,
                    this.departure,
                    this.destination
            ) == 1;
        } catch (SQLException e) {
            if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
                return false;
            } else {
                throw e;
            }
        }
    }
}
