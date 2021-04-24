package models;

import util.Database;

import java.sql.SQLException;

public class Connection {
    String id;
    String flight_no;

    public Connection(String id, String flight_no){
        this.id = id;
        this.flight_no =  flight_no;
    }

    public static boolean createTable() throws SQLException{
        /**
         * sql/CreateConnectionTable.sql
         */
        final String createTableStatement = "CREATE TABLE CONNECTIONS (\n" +
                "     ID CHAR(3),\n" +
                "     FLIGHT_NO CHAR(8),\n" +
                "     PRIMARY KEY (ID, FLIGHT_NO),\n" +
                "     FOREIGN KEY(ID) REFERENCES BOOKINGS,\n" +
                "     FOREIGN KEY(FLIGHT_NO) REFERENCES FLIGHTS\n" +
                ")";
        return Database.fastQuery(createTableStatement);
    }

    public boolean addConnection() throws SQLException{
        /**
         * sql/AddConnection.sql
         */
        final String addConnectionStatement = "INSERT INTO BOOKINGS(\n" +
                "    ID, \n" +
                "    FLIGHT_NO\n" +
                ") VALUE(?, ?)";

        return Database.fastQuery(addConnectionStatement, this.id, this.flight_no);
    }
}
