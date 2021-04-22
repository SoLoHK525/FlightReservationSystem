package util;

import models.Flight;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Debug {
    public static void info(String message) {
        if(Config.DEBUG)
            System.out.println("[DEBUG] [INFO] " + message);
    }

    public static void info(String format, Object... args) {
        if(Config.DEBUG)
            System.out.printf("[DEBUG] [INFO] " + format, args);
    }

    public static void autoFill() {
        try {
            if(Flight.dropTable()) Debug.info("DROPPED TABLE [FLIGHT]");
            if(Flight.createTable()) Debug.info("CREATED TABLE [FLIGHT]");

            Date[] departureDates = new Date[8];
            Date[] arrivalDates = new Date[8];
            int i = 0;
            for(String d : new String[] {
                    "2015-3-15 12:00:00",
                    "2015-3-15 18:30:00",
                    "2015-3-15 10:00:00",
                    "2015-3-15 15:00:00",
                    "2015-3-15 10:00:00",
                    "2015-3-15 4:00:00",
                    "2015-3-15 23:40:00",
                    "2015-3-15 8:00:00",
            }) {
                try {
                    departureDates[i++] = DateTime.convertUtilDateToSqlDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            i = 0;
            for(String d : new String[] {
                    "2015-3-15 16:00:00",
                    "2015-3-15 23:30:00",
                    "2015-3-15 13:00:00",
                    "2015-3-15 18:00:00",
                    "2015-3-15 14:00:00",
                    "2015-3-15 9:00:00",
                    "2015-3-16 3:00:00",
                    "2015-3-15 11:00:00",
            }) {
                try {
                    arrivalDates[i++] = DateTime.convertUtilDateToSqlDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Flight[] flights = new Flight[] {
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
            for(Flight flight : flights) {
                if(flight.addFlight()) ++addedFlight;
            }
            Debug.info("Added %d flights\n", addedFlight);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
