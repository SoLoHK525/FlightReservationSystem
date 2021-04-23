package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateTime {
    public static java.sql.Date convertUtilDateToSqlDate(java.util.Date u) {
        return new java.sql.Date(u.getTime());
    }

    public static java.sql.Date getDate(int year, int month, int day, int hour, int min, int sec) throws InvalidDateException {
        String dateString = String.format("%s-%s-%s %s:%s:%s", year, month, day, hour, min, sec);

        try {
            return convertUtilDateToSqlDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString));
        }catch(ParseException e) {
            throw new InvalidDateException("Invalid Date: " + dateString);
        }
    }

    public static class InvalidDateException extends Exception {
        public InvalidDateException(String message) {
            super(message);
        }
    }
}
