import models.Flight;
import models.Booking;
import models.Connection;
import util.*;
import com.jcraft.jsch.JSchException;

import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

public class FlightReservationSystem {
    public FlightReservationSystem() {
        new CLI();
        new Database();

        if(Config.DEBUG) {
            try {
                Debug.bootstrap();
            } catch (JSchException | SQLException e) {
                e.printStackTrace();
            }
        }else{
            this.boostrap();
        }
        this.run();
    }

    private void run(){
        if(Config.AUTOFILL) {
            Debug.autoFill();
        }

        Debug.info("Program running");
        CLI cli = CLI.getInstance();

        while(true) {
            System.out.println();
            System.out.println("+-------------------------------------+");
            System.out.println("|       Flight Reservation System     |");
            System.out.println("+-------------------------------------+");

            int options = cli.options(new String[] {
                    "Flight management",
                    "Flight search",
                    "Flight booking",
                    "Flight canceling",
                    "exit"
            });

            try {
                switch (options) {
                    case 0:
                        this.flightManagement();
                        break;
                    case 1:
                        this.flightSearch();
                        break;
                    case 2:
                        this.flightBooking();
                        break;
                    case 3:
                        this.flightCanceling();
                        break;
                    case 4:
                        System.out.println("Bye!");
                        this.exit(0);
                        break;
                }
            }catch(SQLException e){
                e.printStackTrace();
                System.out.println("Error code: " + e.getErrorCode());
            }
        }
    }

    public void flightManagement() throws SQLException {
        CLI cli = CLI.getInstance();

        while(true) {
            System.out.println("\nFlight management: ");

            int options = cli.options(new String[]{
                    "Show all flights info",
                    "Show a flight info by flight id",
                    "Add a flight",
                    "Delete a flight",
                    "Back to main menu"
            });

            if(options == 0) {
                // show all flights info
                ArrayList<String> info = Flight.getAllFlightsInfo();
                for(String i : info) {
                    System.out.println("+-----------------+");
                    System.out.println(i);
                    System.out.println("+-----------------+");
                }
            } else if(options == 1) {
                // show a flight info by id
                String flightNo = cli.prompt("Flight No: ");
                String flightInfo = Flight.getFlightInfo(flightNo);
                if(flightInfo.isEmpty()) {
                    System.out.printf("Flight [%s] not found.\n", flightNo);
                }else{
                    System.out.println(flightInfo);
                }
            } else if(options == 2) {
                // Add a flight
                String example = "CX108, 2015/03/15/08:00:00, 2015/03/15/20:00:00, 9000, 3, HK, LA";

                String[] flightInfo = cli.promptSplit("Please input flight information in this format, e.g: \n%s", ",", example);
                if(flightInfo.length < 7) {
                    System.out.println("Not enough parameters");
                    break;
                }

                String flightNo = flightInfo[0];
                Date depart_time, arrive_time;
                int fare, seat_limit;

                try {
                    depart_time = DateTime.getDate(flightInfo[1]);
                    arrive_time = DateTime.getDate(flightInfo[2]);
                }catch(DateTime.InvalidDateException e) {
                    System.out.println("Invalid date format, it should be YYYY/MM/DD/HH:mm:ss");
                    break;
                }
                try {
                    fare = Integer.parseInt(flightInfo[3]);
                    seat_limit = Integer.parseInt(flightInfo[4]);
                }catch(NumberFormatException e) {
                    System.out.println("Invalid numbers for fare or seat_limit");
                    break;
                }

                String departure = flightInfo[5];
                String destination = flightInfo[6];

                Flight f = new Flight(flightNo, departure, destination, depart_time, arrive_time, fare, seat_limit);
                if(f.addFlight()) {
                    System.out.printf("Succeed to add flight %s\n", flightNo);
                }else{
                    System.out.printf("Flight [%s] already exists.\n", flightNo);
                }
            } else if(options == 3) {
                String deleteFlightId = cli.prompt("Flight ID to be deleted: ");

                try {
                    int deletedRows = Flight.deleteFlight(deleteFlightId);

                    if (deletedRows == 0) {
                        System.out.printf("Flight [%s] not found.\n", deleteFlightId);
                    } else {
                        System.out.printf("Succeed to delete the flight %s\n", deleteFlightId);
                    }
                } catch(SQLException e) {
                    if(e.getMessage().contains("FLIGHT_HAS_CONNECTIONS")) {
                        System.out.printf("Failed to delete flight: flight already has connections %s\n", deleteFlightId);
                    }else{
                        throw e;
                    }
                }
            }else if(options == 4) {
                // exit to main menu
                return;
            }
        }
    }

    public void flightSearch() throws SQLException {
        System.out.println("Flight search: ");
        CLI cli = CLI.getInstance();

        String example = "HK, Beijing, 2, 20";
        String[] s = cli.promptSplit("Search a flight path in this format: %s", ",", example);

        if(s.length < 4) {
            System.out.println("Not enough parameters");
            return;
        }

        String source = s[0];
        String destination = s[1];

        try {
            int stop = Integer.parseInt(s[2]);
            int hour = Integer.parseInt(s[3]);

            ArrayList<String> results = Flight.searchFlight(source, destination, stop, hour);

            int i = 0;
            System.out.printf("Total %s choice(s): \n", results.size());

            for(String result : results) {
                System.out.printf("(%d) %s\n", ++i, result);
            }
        }catch(NumberFormatException e) {
            System.out.println("Invalid numbers in either stops or hours");
            return;
        }
    }

    public void flightBooking() throws SQLException {
        CLI cli = CLI.getInstance();
        System.out.println("Flight booking: ");
        String example = "C01, CX105, CX104";
        String[] parameters = cli.promptSplit("Please input CustomerID and Flight Number in this format: " +
                    "e.g. %s", ",", example);
        int numberOfFlights = parameters.length -1;

        if (numberOfFlights <= 0){
            throw new SQLException("Not Enough Parameters");
        }

        int[] fares = new int[numberOfFlights];
        int j = 1;
        double sumOfFare = 0;
        try {
            for (int i = 0; i < fares.length; i++) {
                fares[i] = Flight.getFare(parameters[j]);
                sumOfFare += fares[i];
                j++;
            }
        } catch(SQLException e){
            if (e.getMessage().contains("FLIGHT_DOES_NOT_EXISTS")){
                System.out.println("Not Exist Flight_Number");
                return;
            }else{
                throw e;
            }
        }
        sumOfFare = fareCalculation(numberOfFlights, sumOfFare);
        Database.getConnection().setAutoCommit(false);
        Booking b = new Booking(parameters[0], sumOfFare);
        b.addBooking();
        Connection[] C = new Connection[numberOfFlights];
        j = 1;
        try {
            for (int i = 0; i < numberOfFlights; i++) {
                C[i] = new Connection(b.getId(), parameters[j]);
                j++;
                C[i].addConnection();
            }
        } catch(SQLException e) {
            if(e.getMessage().contains("No Seat Left")) {
                b.deleteTuple();
                System.out.println("Fail to book a flight for " + parameters[0] + ", Book id is " + b.getFormattedId());
                System.out.println("Reason: No Seat Left");
                return;
            }else{
                Database.getConnection().setAutoCommit(true);
                throw e;
            }
        }

        Database.fastQuery("commit");
        System.out.print("Succeed to book a flight for " + parameters[0] + ", Book id is " + b.getFormattedId());
    }

    public double fareCalculation(int numberOfFlights, double sumOfFares){
        if (numberOfFlights == 2){
            sumOfFares = sumOfFares * 0.9;
        }else {
            sumOfFares = sumOfFares * 0.75;
        }
        return sumOfFares;
    }

    public void flightCanceling() throws SQLException{
        CLI cli = CLI.getInstance();
        System.out.println("Flight Canceling: ");
        String example = "C01, B1";
        String[] parameters = cli.promptSplit("Please input CustomerID and Flight Number in this format: " +
                "e.g. %s", ",", example);

        if (parameters.length < 2){
            throw new SQLException("Not Enough Parameters");
        }
        String customerID = parameters[0];
        String bookingID = parameters[1];
        Database.getConnection().setAutoCommit(false);
        try {
            if(!Booking.deleteTuple(bookingID)) {
                throw new SQLException("Not Exist Tuple");
            }
        }catch (SQLException e){
            if (e.getMessage().contains("Not Exist Tuple")){
                System.out.println("Booking " + bookingID + " customer " + customerID + " fails to cancel");
                return;
            } else {
                Database.getConnection().setAutoCommit(true);
                throw e;
            }
        }
        Database.getConnection().setAutoCommit(true);
        Database.fastQuery("commit");

        System.out.println("Booking " + bookingID + " customer " + customerID + " is canceled");
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
        CLI.getInstance().close();
        Database.getInstance().close();

        System.exit(exitCode);
    }
}
