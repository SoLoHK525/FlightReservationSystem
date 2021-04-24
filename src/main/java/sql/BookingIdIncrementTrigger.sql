CREATE OR REPLACE TRIGGER BOOKING_ID_INC
BEFORE INSERT ON BOOKINGS
FOR EACH ROW
BEGIN
    SELECT (COUNT(*) + 1) INTO :new.ID FROM BOOKINGS;
END;
