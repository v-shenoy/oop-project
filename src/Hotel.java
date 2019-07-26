import java.sql.*;

class Hotel
{
	String name, address, location, images;
	double rating;
	int noOfRatings, roomCount, price, wifi, breakfast, rental, hotelID;
	int freeRooms, minRooms;
	Connection conn;

	Hotel(ResultSet rs)
	{
		try
		{
			conn = DatabaseConnection.connect();
			name = rs.getString("name");
			address = rs.getString("address");
			location = rs.getString("location");
			images = rs.getString("images");
			rating = rs.getDouble("rating");
			noOfRatings = rs.getInt("noOfRatings");
			roomCount = rs.getInt("roomCount");
			price = rs.getInt("price");
			wifi = rs.getInt("wifi");
			breakfast = rs.getInt("breakfast");
			rental = rs.getInt("rental");
			hotelID = rs.getInt("rowid");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	void updateRating()
	{
		PreparedStatement pst = null;
		String updateQuery = "UPDATE Hotels SET rating=?, noOfRatings=? WHERE "
		+ "images=?";
		try
		{
			pst = conn.prepareStatement(updateQuery);
			pst.setDouble(1,this.rating);
			pst.setInt(2,this.noOfRatings);
			pst.setString(3,this.images);
			pst.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}