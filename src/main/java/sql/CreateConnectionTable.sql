CREATE TABLE CONNECTIONS (
    ID INT NOT NULL,
    FLIGHT_NO VARCHAR(8) NOT NULL,
    PRIMARY KEY (ID, FLIGHT_NO),
    FOREIGN KEY(ID) REFERENCES BOOKINGS(ID),
    FOREIGN KEY(FLIGHT_NO) REFERENCES FLIGHTS(FLIGHT_NO)
)