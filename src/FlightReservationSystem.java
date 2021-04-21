import Utils.Database;
import Utils.GUI;
import com.jcraft.jsch.JSchException;

import java.sql.SQLException;

public class FlightReservationSystem {
    public FlightReservationSystem() {
        this.boostrap();
    }

    private void boostrap() {
        Database db = new Database();

        if(GUI.promptYesNo("Deploy ssh tunneling?")) {
            String[] input = GUI.promptUsernameAndPassword("Input your ssh credentials");

            try {
                db.useProxy(input[0], input[1]);
            }catch (JSchException e) {
                System.out.println("Failed to connect to ssh: ");
                e.printStackTrace();

                System.exit(-1);
            }
        }

        String[] input = GUI.promptUsernameAndPassword("Input your database credentials");

        try {
            db.connect(input[0], input[1]);
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: ");
            e.printStackTrace();

            System.exit(-1);
        }

    }
}
