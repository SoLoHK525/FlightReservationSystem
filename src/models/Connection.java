package models;

import util.Database;
import util.Debug;

import java.sql.SQLException;

public class Connection {
    String id;
    String flight_no;

    public Connection(String id, String flight_no){
        this.id = id;
        this.flight_no =  flight_no;
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateConnectionTable.sql.sql
         */
        final String createTableStatement = "CREATE TABLE CONNECTIONS (\n" +
                "     ID INT,\n" +
                "     FLIGHT_NO CHAR(8),\n" +
                "     PRIMARY KEY (ID, FLIGHT_NO),\n" +
                "     FOREIGN KEY(ID) REFERENCES BOOKINGS(ID),\n" +
                "     FOREIGN KEY(FLIGHT_NO) REFERENCES FLIGHTS(FLIGHT_NO)\n" +
                ")";

        return Database.fastQuery(createTableStatement) == 0;
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE CONNECTIONS CASCADE CONSTRAINT";

        return Database.fastQuery(dropTableStatement) == 0;
    }

    public int addConnection() throws SQLException{
        /**
         * sql/AddConnection.sql
         */
        final String addConnectionStatement = "INSERT INTO BOOKINGS(\n" +
                "    ID, \n" +
                "    FLIGHT_NO\n" +
                ") VALUE(?, ?)";

        return Database.fastQuery(addConnectionStatement, this.id, this.flight_no);
    }

    public static void autofill() throws SQLException {
        try {
            if (Connection.dropTable()) Debug.info("DROPPED TABLE [CONNECTIONS]");
        }catch(SQLException e) {
            Debug.info("TABLE [CONNECTIONS] DOES NOT EXIST");
        }

        if (Connection.createTable()) Debug.info("CREATED TABLE [CONNECTIONS]");


    }
}
