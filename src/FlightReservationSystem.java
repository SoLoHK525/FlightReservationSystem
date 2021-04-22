import models.Flight;
import util.*;
import util.Database.Response;
import com.jcraft.jsch.JSchException;

import java.sql.SQLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FlightReservationSystem {
    private final Database db;

    public FlightReservationSystem() {
        db = new Database();

        this.boostrap();
        this.run();

    }

    private void run() {
        System.out.println("Program running");

        if(Config.AUTOFILL) {
            Debug.autoFill();
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
