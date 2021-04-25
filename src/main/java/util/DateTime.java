package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateTime {
    /**
     * Convert java.sql.time to java.util.time
     * @param s time to be converted
     * @return
     */
    public static java.util.Date convertSqlDateToUtilDate(java.sql.Date s) { return new java.sql.Date(s.getTime()); }

    /**
     * Convert java.util.time to java.sql.time
     * @param u time to be converted
     * @return
     */
    public static java.sql.Date convertUtilDateToSqlDate(java.util.Date u) {
        return new java.sql.Date(u.getTime());
    }

    /**
     * Format date to yyyy-MM-dd HH:mm:ss
     * @param d
     * @return The formatted date
     */
    public static String formatDate(java.sql.Date d) {
        java.util.Date date = convertSqlDateToUtilDate(d);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * Get date from string format: yyyy-MM-dd HH:mm:ss
     * @param dateString string
     * @return Date Object
     */
    public static java.sql.Date getDate(String dateString) throws InvalidDateException {
        try {
            return convertUtilDateToSqlDate(new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss").parse(dateString));
        }catch(ParseException e) {
            throw new InvalidDateException("Invalid Date: " + dateString);
        }
    }

    /**
     * Get Date
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @param sec
     * @return Date Object
     * @throws InvalidDateException
     */
    public static java.sql.Date getDate(int year, int month, int day, int hour, int min, int sec) throws InvalidDateException {
        String dateString = String.format("%s/%s/%s/%s:%s:%s", year, month, day, hour, min, sec);

        return getDate(dateString);
    }

    public static class InvalidDateException extends Exception {
        public InvalidDateException(String message) {
            super(message);
        }
    }
}
