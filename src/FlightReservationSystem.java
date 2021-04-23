import models.Flight;
import util.*;
import util.Database.Response;
import com.jcraft.jsch.JSchException;

import java.sql.SQLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FlightReservationSystem {
    public FlightReservationSystem() {
        new Database();

        if(Config.DEBUG) {
            try {
                this.debugBootstrap();
            } catch (JSchException | SQLException e) {
                e.printStackTrace();
            }
        }else{
            this.boostrap();
        }
        this.run();
    }

    private void run() {
        System.out.println("Program running");

        if(Config.AUTOFILL) {
            Debug.autoFill();
        }

        this.exit(0);
    }

    public static void debugBootstrap() throws JSchException, SQLException {
        String proxyUser = System.getenv("PROXYUSER");
        String proxyPassword = System.getenv("PROXYPASSWORD");
        String dbUser = System.getenv("DBUSER");
        String dbPassword = System.getenv("DBPASSWORD");

        if (proxyUser == null || proxyUser.isEmpty()) {
            throw new RuntimeException("PROXYUSER is empty");
        }

        if (proxyPassword == null || proxyPassword.isEmpty()) {
            throw new RuntimeException("PROXYUSER is empty");
        }

        if (dbUser == null || dbUser.isEmpty()) {
            throw new RuntimeException("DBUSER is empty");
        }

        if (dbPassword == null || dbPassword.isEmpty()) {
            throw new RuntimeException("DBPASSWORD is empty");
        }

        Database.getInstance().useProxy(proxyUser, proxyPassword).connect(dbUser, dbPassword);
    }

    private void boostrap() {
        if(GUI.promptYesNo("Deploy ssh tunneling?")) {
            String[] input = GUI.promptUsernameAndPassword("Input your ssh credentials");

            try {
                Database.getInstance().useProxy(input[0], input[1]);

                Debug.info("Proxy created");
            }catch (JSchException e) {
                System.out.println("Failed to connect to ssh: ");
                e.printStackTrace();

                this.exit(-1);
            }
        }

        String[] input = GUI.promptUsernameAndPassword("Input your database credentials");

        try {
            Database.getInstance().connect(input[0], input[1]);

            Debug.info("Connected to database");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: ");
            e.printStackTrace();

            this.exit(-1);
        }
    }

    public void exit(int exitCode) {
        Database.getInstance().close();
        System.exit(exitCode);
    }
}
