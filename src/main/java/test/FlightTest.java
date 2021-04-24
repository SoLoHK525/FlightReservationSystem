package test;

import models.Flight;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import util.Database;
import util.DateTime;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testing Flight Class")
class FlightTest implements IDatabaseTest {
    @Test
    @DisplayName("Drop Table")
    @Order(1)
    void dropTable() throws SQLException {
        Assertions.assertEquals(true, Flight.dropTable());
    }

    @Test
    @DisplayName("Create Table")
    @Order(2)
    void createTable() throws SQLException {
        assertEquals(true, Flight.createTable());
    }

    @Test
    @DisplayName("Adding Flights")
    @Order(4)
    void addFlight() throws SQLException, DateTime.InvalidDateException {
        ArrayList<String> flightsInfo = Flight.getAllFlightsInfo();
        int len = flightsInfo.size();

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

        for (Flight flight : flights) {
            assertEquals(true, flight.addFlight());
        }

        Database.Response res = Database.query("SELECT Flight_No FROM FLIGHT");
        String buffer = "";

        while(res.resultSet.next()){
            buffer += res.resultSet.getString(1).trim() + " ";
        }

        res.close();

        assertEquals("CX100 CX101 CX102 CX103 CX104 CX105 CX106 CX107", buffer.trim());
        assertEquals(len, flightsInfo.size());
    }

    @Test
    @Order(5)
    @DisplayName("Get Flight Info")
    void addExistedFlight() throws SQLException, DateTime.InvalidDateException {
        Flight f = new Flight("CX100",
                "HK",
                "Tokyo",
                DateTime.getDate(2015, 3, 15, 12, 0, 0),
                DateTime.getDate(2015, 3, 15, 16, 0, 0), 2000, 3
        );

        assertEquals(false, f.addFlight());
    }

    @Test
    @Order(5)
    @DisplayName("Get Flight Info")
    void getFlightInfo() throws SQLException {
        String info = Flight.getFlightInfo("CX100");

        String expected = "Flight_no: CX100\n" +
                "Depart_Time: 2015-03-15 12:00:00\n" +
                "Arrive_Time 2015-03-15 16:00:00\n" +
                "Fare: 2000\n" +
                "Seat Limit: 3\n" +
                "Source: HK\n" +
                "Dest: Tokyo";
        assertEquals(expected, info);
    }

    @Test
    @Order(6)
    @DisplayName("Delete flight")
    void deleteFlight() throws SQLException {
        assertEquals(1, Flight.deleteFlight("CX100"));
    }

    @Test
    @Order(7)
    @DisplayName("Delete not exist flight")
    void deleteNotExistFlight() throws SQLException {
        assertEquals(0, Flight.deleteFlight("XD123"));
    }
}