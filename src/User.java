import java.util.*;
import java.sql.*;

public class User
{
	String name, username, dob, email, address;
	Search search;
	int bookingRef;
	Connection conn;
	User(String name, String username, String dob, String email, String address, int bookingRef)
	{
		this.name = name;
		this.username = username;
		this.dob = dob;
		this.email = email;
		this.address = address;
		this.bookingRef =  bookingRef;
		this.search = null;
	}

	// Instantiates the user's search query.
	void setQuery(String location, java.util.Date checkinDate, java.util.Date checkoutDate, 
		int rooms, int people)
	{
		search = new Search(location, checkinDate, checkoutDate, rooms, people);
	}

	// Updates a user's booking reference in the Users table.
	void updateBooking(int bookingRef)
	{
		this.bookingRef = bookingRef;
		String updateRef = "UPDATE Users SET bookingRef =? WHERE username =?";
		try
		{
			conn = DatabaseConnection.connect();
			PreparedStatement pst = conn.prepareStatement(updateRef);
			pst.setInt(1,bookingRef);
			pst.setString(2, username);
			pst.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class Search
{
	String location;
	int rooms, people;
	java.util.Date checkinDate, checkoutDate;

	Search(String location, java.util.Date checkinDate, java.util.Date checkoutDate,
	 int rooms, int people)
	{
		this.location = location;
		this.checkinDate = checkinDate;
		this.checkoutDate = checkoutDate;
		this.rooms = rooms;
		this.people = people;
	}
}