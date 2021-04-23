package util;

import models.Flight;

import java.sql.Date;
import java.sql.SQLException;

public class Debug {
    public static void info(String message) {
        if (Config.DEBUG)
            System.out.println("[DEBUG] [INFO] " + message);
    }

    public static void info(String format, Object... args) {
        if (Config.DEBUG)
            System.out.printf("[DEBUG] [INFO] " + format, args);
    }

    public static void autoFill() {
        try {
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

            int addedFlight = 0;
            for (Flight flight : flights) {
                if (flight.addFlight()) ++addedFlight;
            }

            Debug.info("Added %d flights\n", addedFlight);
        } catch (SQLException | DateTime.InvalidDateException e) {
            e.printStackTrace();
        }
    }
}
