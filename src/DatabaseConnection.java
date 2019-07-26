import java.sql.*;
import javax.swing.*;

public class DatabaseConnection
{

    static Connection conn = null;
	static Connection connect()
	{

        if(conn != null)
        {
            // Returns the connection object to the database if it already exists
            return conn;
        }
        else
        {
            // Creates the Users, Hotels, Bookings table in the database Portal.db
            // and returns the connection object
            String url = "jdbc:sqlite:./resources/Portal.db";
            String createUserTable ="CREATE TABLE IF NOT EXISTS Users("
                + "name TEXT NOT NULL,"
                + "dob TEXT NOT NULL,"
                + "address TEXT NOT NULL,"
                + "email TEXT NOT NULL,"
                + "username TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "bookingRef INT NOT NULL,"
                + "PRIMARY KEY (username));";

            String createHotelTable = "CREATE TABLE IF NOT EXISTS Hotels("
                + "name TEXT NOT NULL,"
                + "location TEXT NOT NULL,"
                + "address TEXT NOT NULL,"
                + "rating REAL NOT NULL,"
                + "noOfRatings INT NOT NULL,"
                + "roomCount INT NOT NULL,"
                + "price INT NOT NULL,"
                + "wifi INT NOT NULL,"
                + "breakfast INT NOT NULL,"
                + "rental int NOT NULL,"
                + "images TEXT NOT NULL);";

            String createBookingTable = "CREATE TABLE IF NOT EXISTS Bookings("
                + "username TEXT NOT NULL,"
                + "hotelID INT NOT NULL,"
                + "roomCount INT NOT NULL,"
                + "checkinDate INT NOT NULL,"
                + "checkoutDate INT NOT NULL,"
                + "status INT NOT NULL,"
                + "rated INT NOT NULL,"
                + "pan TEXT NOT NULL);";

            try
    		{
                conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement();
                stmt.execute(createUserTable);
                stmt.execute(createHotelTable);
                stmt.execute(createBookingTable);
                return conn;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            	return null;
            }
        }
	}
}