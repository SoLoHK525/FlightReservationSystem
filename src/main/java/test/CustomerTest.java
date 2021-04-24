package test;

import models.Customer;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testing Customer Class")
class CustomerTest implements IDatabaseTest {
    @Test
    @DisplayName("Drop Table")
    @Order(1)
    void dropTable() throws SQLException {
        Assertions.assertEquals(true, Customer.dropTable());
    }

    @Test
    @DisplayName("Create Table")
    @Order(2)
    void createTable() throws SQLException {
        assertEquals(true, Customer.createTable());
    }
}