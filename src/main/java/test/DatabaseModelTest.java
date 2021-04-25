package test;

import models.Booking;
import models.Connection;
import models.Customer;
import models.Flight;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import util.Database;
import util.DateTime;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database Model Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseModelTest implements IDatabaseTest {
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Composed Data Tests")
    class ComposeTest {
        @Test
        @DisplayName("Add Bookings")
        @Order(1)
        void addBooking() throws SQLException {
            Database.getConnection().setAutoCommit(false);
            Booking booking = new Booking("C01", 1000);
            booking.addBooking();

            Connection[] connections = new Connection[] {
                    new Connection(booking.getId(), "CX104"),
                    new Connection(booking.getId(), "CX105"),
            };

            for(Connection c : connections) {
                assertEquals(1, c.addConnection());
            }
            Database.getConnection().setAutoCommit(true);
        }

        @Test
        @DisplayName("Add Bookings on Invalid Flight")
        @Order(2)
        void addBookingOnInvalidFlight() throws SQLException {
            Database.getConnection().setAutoCommit(false);
            Booking booking = new Booking("C02", 1000);
            booking.addBooking();

            Connection[] connections = new Connection[] {
                    new Connection(booking.getId(), "KO456"),
                    new Connection(booking.getId(), "XD123"),
            };

            for(Connection c : connections) {
                Exception exception = assertThrows(SQLException.class, () -> {
                    c.addConnection();
                });

                String expectedMessage = "no data found";
                String actualMessage = exception.getMessage();

                assertTrue(actualMessage.contains(expectedMessage));
            }

            Database.getConnection().setAutoCommit(true);
        }

        @Test
        @DisplayName("Delete Flight that has a booking")
        @Order(3)
        void deleteFlightWithBookings() {
            Exception exception = assertThrows(SQLException.class, () -> {
                Flight.deleteFlight("CX104");
            });

            String expectedMessage = "FLIGHT_HAS_CONNECTIONS";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Testing Flight Model")
    class FlightTest {
        @Test
        @DisplayName("Drop Table")
        @Order(1)
        void dropTable() throws SQLException {
            assertTrue(Flight.dropTable());
        }

        @Test
        @DisplayName("Create Table")
        @Order(2)
        void createTable() throws SQLException {
            assertTrue(Flight.createTable());
        }

        @Test
        @DisplayName("Create Trigger")
        @Order(2)
        void createTrigger() throws SQLException {
            assertTrue(Flight.createTrigger());
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
                assertTrue(flight.addFlight());
            }

            Database.Response res = Database.query("SELECT Flight_No FROM FLIGHT");
            String buffer = "";

            while (res.resultSet.next()) {
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
                    DateTime.getDate(2015, 3, 15, 16, 0, 0),
                    2000,
                    3
            );

            assertFalse(f.addFlight());
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

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Testing Customer Model")
    class CustomerTest {
        @Test
        @DisplayName("Drop Table")
        @Order(1)
        void dropTable() throws SQLException {
            assertTrue(Customer.dropTable());
        }

        @Test
        @DisplayName("Create Table")
        @Order(2)
        void createTable() throws SQLException {
            assertTrue(Customer.createTable());
        }

        @Test
        @DisplayName("Add Customer")
        @Order(3)
        void addCustomer() throws SQLException {
            Customer[] customers = new Customer[] {
                    new Customer("C01", "Alice", "CHN", "P1234567"),
                    new Customer("C02", "Bob", "UK", "P1111111"),
                    new Customer("C03", "Cole", "US", "P7654321")
            };

            for(Customer c : customers) {
                assertTrue(c.addCustomer() >= 0);
            }
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Testing Connection Model")
    class ConnectionTest {
        @Test
        @DisplayName("Drop Table")
        @Order(1)
        void dropTable() throws SQLException {
            assertTrue(Connection.dropTable());
        }

        @Test
        @DisplayName("Create Table")
        @Order(2)
        void createTable() throws SQLException {
            assertTrue(Connection.createTable());
        }

        @Test
        @DisplayName("Create Trigger")
        @Order(2)
        void createTrigger() throws SQLException {
            assertTrue(Connection.createCancelBookingTrigger());
            assertTrue(Connection.createCheckSeatLimitTrigger());
        }
    }
}