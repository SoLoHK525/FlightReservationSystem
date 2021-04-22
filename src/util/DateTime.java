package util;

public class DateTime {
    public static java.sql.Date convertUtilDateToSqlDate(java.util.Date u) {
        return new java.sql.Date(u.getTime());
    }
}
