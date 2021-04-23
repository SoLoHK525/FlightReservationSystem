package models;

import com.jcraft.jsch.JSchException;
import util.Database;
import util.DateTime;
import util.Debug;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlightTest {

    @org.junit.jupiter.api.Test
    void addFlight() throws JSchException, SQLException, DateTime.InvalidDateException {
        String proxyUser = System.getenv("PROXYUSER");
        String proxyPassword = System.getenv("PROXYPASSWORD");
        String dbUser = System.getenv("DBUSER");
        String dbPassword = System.getenv("DBPASSWORD");

        if (proxyUser == null || proxyUser.isEmpty()) {
            throw new RuntimeException("PROXYUSER is empty");
        }

        if (proxyPassword == null || proxyPassword.isEmpty()) {
            throw new RuntimeException("PROXYUSER is empty");
        }

        if (dbUser == null || dbUser.isEmpty()) {
            throw new RuntimeException("DBUSER is empty");
        }

        if (dbPassword == null || dbPassword.isEmpty()) {
            throw new RuntimeException("DBPASSWORD is empty");
        }

        new Database().useProxy(proxyUser, proxyPassword).connect(dbUser, dbPassword);

        if (Flight.dropTable()) Debug.info("DROPPED TABLE [FLIGHT]");
        if (Flight.createTable()) Debug.info("CREATED TABLE [FLIGHT]");

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
            flight.addFlight();
        }

        Database.Response res = Database.query("SELECT Flight_No FROM FLIGHT");
        String buffer = "";
        while(res.resultSet.next()){
            buffer += res.resultSet.getString(1).trim();
        }

        assertEquals("CX100CX101CX102CX103CX104CX105CX106CX107", buffer);
    }
}