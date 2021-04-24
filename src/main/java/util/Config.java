package util;

public class Config {
    // Toggle Debug Output
    public static boolean DEBUG = false;

    // Auto Filling Test Data to database
    public static boolean AUTOFILL = false;

    public final static String databaseHost = "orasrv1.comp.hkbu.edu.hk";
    public final static int databasePort = 1521;

    public final static String database = "pdborcl.orasrv1.comp.hkbu.edu.hk";

    public final static String proxyHost = "faith.comp.hkbu.edu.hk";
    public final static int proxyPort = 22;

    public final static String forwardHost = "localhost";
}
