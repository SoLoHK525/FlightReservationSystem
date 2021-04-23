import models.Flight;
import util.Database;
import util.DateTime;
import util.Debug;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Example {
    public static void addFlight() {
        try {
            Date departureTime = DateTime.convertUtilDateToSqlDate(new SimpleDateFormat("yyyy/MM/ddHH:mm:ss").parse("2020/05/2523:10:10"));
            Date arrivalTime = DateTime.convertUtilDateToSqlDate(new SimpleDateFormat("yyyy/MM/ddHH:mm:ss").parse("2020/05/2610:32:10"));

            Flight f = new Flight("VA105", "Hong Kong", "Tokyo", departureTime, arrivalTime, 3000, 50);

            if(f.addFlight()) {
                Debug.info("Added Flight");
            }
        }catch(SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static void getFlightNumbers() {
        try {
            Database.Response res = Database.query("SELECT Flight_No FROM FLIGHT");

            System.out.println("Flights:");
            while (res.resultSet.next()) {
                System.out.println(res.resultSet.getString(1));
            }

            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
