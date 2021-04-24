import util.Config;
import util.Debug;

public class Program {
    public static void main(String args[]) {
        for(String arg : args) {
            if(arg.equalsIgnoreCase("--debug")) {
                Config.DEBUG = true;
                Debug.info("Toggled debug mode");
            }

            if(arg.equalsIgnoreCase("--autofill")) {
                Config.AUTOFILL = true;
                Debug.info("Toggled auto fill");
            }
        }
        new FlightReservationSystem();
    }
}
