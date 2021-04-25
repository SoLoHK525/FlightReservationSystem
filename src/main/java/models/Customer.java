package models;

import util.Database;
import util.Debug;

import java.sql.SQLException;

public class Customer {
    String id;
    String name;
    String nationality;
    String passport_num;

    public Customer(String id, String name, String nationality, String passport_num) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.passport_num = passport_num;
    }

    public int addCustomer() throws SQLException {
        /**
         * sql/AddCustomer.sql
         */
        final String addCustomerStatement = "INSERT INTO CUSTOMERS (\n" +
                "    ID, NAME, NATIONALITY, PASSPORT_NO\n" +
                ") VALUES (?, ?, ?, ?)";

        return Database.fastQuery(addCustomerStatement, this.id, this.name, this.nationality, this.passport_num);
    }

    public static boolean truncateTable() throws SQLException {
        final String truncateTableStatement = "TRUNCATE TABLE CUSTOMERS";

        return Database.fastQuery(truncateTableStatement) == 0;
    }

    public static boolean dropTable() throws SQLException {
        final String dropTableStatement = "DROP TABLE CUSTOMERS CASCADE CONSTRAINT";

        try {
            return Database.fastQuery(dropTableStatement) == 0;
        } catch (SQLException e) {
            if(e.getMessage().contains("table or view does not exist")) {
                return true;
            }else{
                throw e;
            }
        }
    }

    public static boolean createTable() throws SQLException {
        /**
         * sql/CreateCustomerTable.sql
         */

        final String createTableStatement = "CREATE TABLE CUSTOMERS (\n" +
                "    ID CHAR(6) NOT NULL,\n" +
                "    NAME VARCHAR(32) NOT NULL,\n" +
                "    NATIONALITY CHAR(5)  NOT NULL,\n" +
                "    PASSPORT_NO CHAR(8) NOT NULL,\n" +
                "    PRIMARY KEY (ID),\n" +
                "    UNIQUE (PASSPORT_NO)\n" +
                ")";

        return Database.fastQuery(createTableStatement) == 0;
    }

    public static void autofill() throws SQLException {
        try {
            if (Customer.dropTable()) Debug.info("DROPPED TABLE [CUSTOMERS]");
        }catch(SQLException e) {
            e.printStackTrace();
            Debug.info("TABLE [CUSTOMERS] DOES NOT EXIST");
        }

        if (Customer.createTable()) Debug.info("CREATED TABLE [CUSTOMERS]");

        Customer[] customers = new Customer[] {
                new Customer("C01", "Alice", "CHN", "P1234567"),
                new Customer("C02", "Bob", "UK", "P1111111"),
                new Customer("C03", "Cole", "US", "P7654321")
        };

        int addedCustomer = 0;
        for(Customer c : customers) {
            if(c.addCustomer() >= 0) ++addedCustomer;
        }

        Debug.info("Added %d customers\n", addedCustomer);
    }
}
