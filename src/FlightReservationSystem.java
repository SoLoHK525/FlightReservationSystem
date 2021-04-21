import Utils.Config;
import Utils.Database;
import Utils.Debug;
import Utils.GUI;
import com.jcraft.jsch.JSchException;

import java.sql.SQLException;

public class FlightReservationSystem {
    Database db;

    public FlightReservationSystem() {
        this.boostrap();
        this.run();
    }

    private void run() {
        System.out.println("Program running");
        this.exit(0);
    }

    private void boostrap() {
        db = new Database();

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
