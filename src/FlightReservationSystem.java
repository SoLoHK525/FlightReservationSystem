import util.Database;
import util.Debug;
import util.GUI;
import com.jcraft.jsch.JSchException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class FlightReservationSystem {
    private final Database db;

    public FlightReservationSystem() {
        db = new Database();

        this.boostrap();
        this.run();

    }

    private void run() {
        System.out.println("Program running");

        if(Database.fastQuery("INSERT INTO FLIGHT " +
                            "(Flight_No, Depart, Arrive, Fare, Seat_Limit, Source, Dest) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    "VA120",
                    new Date(2021, 11, 21),
                    new Date(2021, 11, 22),
                    1000,
                    50,
                    "Hong Kong",
                    "Tokyo"
        )) Debug.info("Inserted");

        Database.Response res = Database.query("SELECT Flight_No FROM FLIGHT");

        try {
            while (res.resultSet.next()) {
                System.out.println(res.resultSet.getString(1));
            }

            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.exit(0);
    }

    private void boostrap() {
        if(GUI.promptYesNo("Deploy ssh tunneling?")) {
            String[] input = GUI.promptUsernameAndPassword("Input your ssh credentials");

            try {
                db.useProxy(input[0], input[1]);

                Debug.info("Proxy created");
            }catch (JSchException e) {
                System.out.println("Failed to connect to ssh: ");
                e.printStackTrace();

                this.exit(-1);
            }
        }

        String[] input = GUI.promptUsernameAndPassword("Input your database credentials");

        try {
            db.connect(input[0], input[1]);

            Debug.info("Connected to database");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: ");
            e.printStackTrace();

            this.exit(-1);
        }
    }

    public void exit(int exitCode) {
        db.close();

        System.exit(exitCode);
    }
}
