package Utils;

public class Debug {
    public static void info(String message) {
        if(Config.DEBUG)
            System.out.println("[DEBUG] [INFO] " + message);
    }
}
