package util;

public class Config {
    // Toggle Debug Output
    public static boolean DEBUG = false;

    // Auto Filling Test Data to database
    public static boolean AUTOFILL = false;

    // hostname to database
    public final static String databaseHost = "orasrv1.comp.hkbu.edu.hk";
    public final static int databasePort = 1521;

    // domain of database
    public final static String database = "pdborcl.orasrv1.comp.hkbu.edu.hk";

    // ssh info for ssh tunneling
    public final static String proxyHost = "faith.comp.hkbu.edu.hk";
    public final static int proxyPort = 22;

    // listening address for ssh-tunneling
    public final static String forwardHost = "localhost";
}
