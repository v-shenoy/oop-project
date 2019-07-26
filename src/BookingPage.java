import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.text.*;
import com.javaswingcomponents.framework.painters.configurationbound.GradientColorPainter;
import com.javaswingcomponents.framework.painters.configurationbound.SolidColorPainter;
import com.javaswingcomponents.framework.painters.gloss.Gloss;
import com.javaswingcomponents.framework.shapes.ShapeProvider;
import com.javaswingcomponents.framework.shapes.StarShape;
import com.javaswingcomponents.rater.JSCRater;
import com.javaswingcomponents.rater.RaterAwareShapeProvider;
import com.javaswingcomponents.rater.listener.RatingChangeEvent;
import com.javaswingcomponents.rater.listener.RatingChangeListener;
import com.javaswingcomponents.rater.model.DefaultRaterModel;
import com.javaswingcomponents.rater.model.DefaultRaterModel.Selection;
import com.javaswingcomponents.rater.plaf.RaterUI;
import com.javaswingcomponents.rater.plaf.basic.BasicRaterUI;
import java.math.*;
import java.util.Properties;
import org.jdatepicker.*;
import org.jdatepicker.util.*;
import org.jdatepicker.impl.*;

public class BookingPage extends Portal
{
	private User user;
	private Hotel h;
	private JButton backButton, modifyButton, cancelBooking, cancelWait, finaliseButton;
	private JTextField pan, checkin, checkout;
	private JSlider rooms, people;

	BookingPage(User user, Hotel h)
	{
		super();
		this.user = user;
		this.h = h;
		setWindowProperties();
	}

	void setWindowProperties()
	{
		super.setWindowProperties();
		int WIDTH = 670;
		int HEIGHT = 500;
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle(h.name + " info");

		Box mainBox = Box.createVerticalBox();
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		Box hotelBox = Box.createVerticalBox();
		hotelBox.setPreferredSize(new Dimension(620,430));
		hotelBox.setMinimumSize(new Dimension(620,430));
		hotelBox.setMaximumSize(new Dimension(620,430));
		hotelBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		JPanel mainPanel = null;

		// Add name of the hotel
		JLabel hotelName = new JLabel(h.name);
		hotelName.setFont(new Font("Georgia", Font.BOLD, 18));
		hotelBox.add(hotelName);
		hotelBox.add(Box.createRigidArea(new Dimension(0,5)));

		// Add images of the rooms
		int imageWIDTH = 200;
		int imageHEIGHT = 200;
		File directory = new File(h.images);
		File contents[] = directory.listFiles();
		Box imageBox = Box.createHorizontalBox();
		imageBox.setPreferredSize(new Dimension(630,210));
		imageBox.setMinimumSize(new Dimension(630,210));
		imageBox.setMaximumSize(new Dimension(630,210));
		try
		{
			for(File image : contents)
			{
				BufferedImage picture = ImageIO.read(image);
				JLabel hotelPic = new JLabel(new ImageIcon(picture.getScaledInstance(
				imageWIDTH, imageHEIGHT, Image.SCALE_FAST)));
				imageBox.add(hotelPic);
				imageBox.add(Box.createRigidArea(new Dimension(5,0)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		imageBox.add(Box.createRigidArea(new Dimension(5,0)));
		imageBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		hotelBox.add(imageBox);
		hotelBox.add(Box.createRigidArea(new Dimension(0,5)));

		// Rating
		JPanel ratingPanel = new JPanel();
		JSCRater rater = new JSCRater();
		rater.setRating(new BigDecimal(h.rating));
		howToChangeThePainters(rater);
		howToListenForChanges(rater, h);
		ratingPanel.add(rater);
		ratingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		ratingPanel.setPreferredSize(new Dimension(135,40));
		ratingPanel.setMinimumSize(new Dimension(135,40));
		ratingPanel.setMaximumSize(new Dimension(135,40));
		hotelBox.add(ratingPanel);
		DecimalFormat df = new DecimalFormat("#.####");
		JLabel ratingLabel = new JLabel("Rating: " + df.format(h.rating) + "/5 from " 
			+ h.noOfRatings + " users.");
		hotelBox.add(ratingLabel);

		// Address
		JLabel hotelAddress = new JLabel("Address: " + h.address);
		hotelAddress.setToolTipText(h.address);
		hotelBox.add(hotelAddress);
		hotelBox.add(Box.createRigidArea(new Dimension(0,5)));

		// Facilities
		String facilities = "Facilities: ";
		int c = 0;
		if(h.wifi == 1)
		{
			facilities += "Wifi";
			c++;
		}
		if(h.breakfast == 1)
		{	
			if(c != 0)
			{
				facilities += ", Breakfast";
			}
			else
			{
				facilities += "Complementary breakfast";
			}
		}
		if(h.rental == 1)
		{
			if(c!= 0)
			{
				facilities += ", Rental Car Service";
			}
			else
			{
				facilities += "Rental Car Service";	
			}
		}
		JLabel hotelFacilities = new JLabel(facilities);
		hotelBox.add(hotelFacilities);
		hotelBox.add(Box.createRigidArea(new Dimension(0,5)));

		String overlappingBookings = "SELECT rowid, * FROM Bookings where hotelID = ? AND"
		+" checkinDate <= ? AND checkoutDate >= ? AND status = 1;";

		int bookedRooms = 0;
		h.freeRooms = h.roomCount;
		h.minRooms = 0;
		PreparedStatement pst;
		try
		{
			pst = conn.prepareStatement(overlappingBookings);
			pst.setInt(1,h.hotelID);
			pst.setDouble(2,user.search.checkoutDate.getTime());
			pst.setDouble(3,user.search.checkinDate.getTime());
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				bookedRooms += rs.getInt("roomCount");
			}
			h.freeRooms -= bookedRooms;
			JLabel availableRooms = new JLabel(h.freeRooms + " rooms available during the" 
				+ " specified dates.");
			hotelBox.add(availableRooms);
			h.minRooms = java.lang.Math.min(user.search.rooms, h.freeRooms);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		String existingBooking = "SELECT rowid, * FROM Bookings WHERE rowid =?;";

		try
		{
			pst = conn.prepareStatement(existingBooking);
			pst.setInt(1,user.bookingRef);
			ResultSet rs = pst.executeQuery();
			JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if(!rs.next() || rs.getInt("status") == 0 || rs.getInt("status") == 3)
			{
				if(h.freeRooms > 0)
				{
					JLabel priceLabel = new JLabel("Cost of booking " + h.minRooms +
				"	 rooms @" + h.price + " each is Rs. " + h.minRooms * h.price +" per night.");
					hotelBox.add(priceLabel);
				}
				JLabel panLabel = new JLabel("PAN No.");
				pan = new JTextField("Enter PAN no.", 15);
				pan.setToolTipText("Entering PAN number is mandatory.");
				if(user.search.rooms > h.freeRooms)
				{
					finaliseButton = new JButton("Waitlist");
				}
				else
				{
					finaliseButton = new JButton("Book.");	
				}
				finaliseButton.setBackground(new Color(60,203,37));
				bookingPanel.setPreferredSize(new Dimension(450,35));
				bookingPanel.setMinimumSize(new Dimension(450,35));
				bookingPanel.setMaximumSize(new Dimension(450,35));
				finaliseButton.addActionListener(new ListenForButton());
				bookingPanel.add(panLabel);
				bookingPanel.add(pan);
				bookingPanel.add(finaliseButton);
				bookingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				hotelBox.add(bookingPanel);
				if(h.minRooms > 0)
				{
					hotelBox.add(new JLabel("To book the available number of rooms go back" 
					+ " to search page and modify the search."));
				}
			}
			else if(rs.getInt("hotelID") != h.hotelID)
			{
				hotelBox.setPreferredSize(new Dimension(620,430));
				hotelBox.setMinimumSize(new Dimension(620,430));
				hotelBox.setMaximumSize(new Dimension(620,430));
				JLabel info1 = new JLabel("You are currently booked or waitlisted in another "
				+ "hotel.");
				JLabel info2 = new JLabel("To make a different booking, you would have to "
					+"cancel that.");
				hotelBox.add(info1);
				hotelBox.add(info2);
			}
			else
			{
				if(rs.getInt("status") == 2)
				{
					bookingPanel.add(new JLabel("Currently waitlisted in this hotel."));
					cancelWait = new JButton("Cancel waitlisting.");
					cancelWait.setBackground(new Color(211,27,27));
					cancelWait.addActionListener(new ListenForButton());
					bookingPanel.add(cancelWait);
					bookingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					hotelBox.add(bookingPanel);

				}
				else if(rs.getInt("status") == 1)
				{
					java.util.Date cin = new java.util.Date(rs.getLong("checkinDate"));
					java.util.Date cout = new java.util.Date(rs.getLong("checkoutDate"));
					bookingPanel.add(new JLabel("You have currently booked " + rs.getInt("roomCount")
						+ " rooms in this hotel."));
					bookingPanel.add(new JLabel("Duration: " + cin + " to " + cout +"."));
					cancelBooking = new JButton("Cancel booking.");
					cancelBooking.setBackground(new Color(211,27,27));
					cancelBooking.addActionListener(new ListenForButton());
					bookingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					
					long daysTill = Portal.daysFromXtoY(new  java.util.Date(), 
						new Date(rs.getLong("checkinDate")));
					long bookedDays = Portal.daysFromXtoY(new java.util.Date(rs.getLong("checkinDate")),
						new java.util.Date(rs.getLong("checkoutDate")));

					if(daysTill <3)
					{
						bookingPanel.add(cancelBooking);
						bookingPanel.add(new JLabel("Cancellation charges: " + 0.5*h.price*bookedDays));
					}
					else
					{
						// Modification Panel
						mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
						mainPanel.setPreferredSize(new Dimension(620,140));
						mainPanel.setMinimumSize(new Dimension(620,140));
						mainPanel.setMaximumSize(new Dimension(620,140));
						bookingPanel.add(cancelBooking);
						bookingPanel.add(new JLabel("Cancellation charges: Rs. 0"));
						JPanel checkinPanel = new JPanel();
						checkinPanel.setPreferredSize(new Dimension(300,45));
						checkinPanel.setMaximumSize(new Dimension(300,45));
						checkinPanel.setMinimumSize(new Dimension(300,45));
						UtilDateModel model1 = new UtilDateModel();
						Properties p = new Properties();
						p.put("text.today", "Today");
						p.put("text.month", "Month");
						p.put("text.year", "Year");
						JLabel checkinLabel = new JLabel("Check-In: ");
						JDatePanelImpl datePanel1 = new JDatePanelImpl(model1, p);
						JDatePickerImpl checkinPicker = new JDatePickerImpl(datePanel1, new DateComponentFormatter());
						checkinPicker.setSize(5,5);
						checkin = checkinPicker.getJFormattedTextField();
						checkin.setToolTipText("Pick a check-in date.");
						checkinPanel.add(checkinLabel);
						checkinPanel.add(checkinPicker);
						checkinPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

						JPanel checkoutPanel = new JPanel();
						checkoutPanel.setPreferredSize(new Dimension(300,45));
						checkoutPanel.setMaximumSize(new Dimension(300,45));
						checkoutPanel.setMinimumSize(new Dimension(300,45));
						JLabel checkoutLabel = new JLabel("Check-Out: ");
						UtilDateModel model2 = new UtilDateModel();
						JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, p);
						JDatePickerImpl checkoutPicker = new JDatePickerImpl(datePanel2, new DateComponentFormatter());
						checkoutPicker.setSize(5,5);
						checkout = checkoutPicker.getJFormattedTextField();
						checkout.setToolTipText("Pick a check-out date.");
						checkoutPanel.add(checkoutLabel);
						checkoutPanel.add(checkoutPicker);
						checkoutPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

						JPanel roomsPanel = new JPanel();
						JLabel roomsLabel = new JLabel("Rooms: ");
						rooms = new JSlider(1, 15, 1);
						rooms.setMinorTickSpacing(1);
						rooms.setMajorTickSpacing(2);
						rooms.setPaintTicks(true);
						rooms.setPaintLabels(true);
						roomsPanel.add(roomsLabel);
						roomsPanel.add(rooms);
						roomsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
						mainPanel.add(checkinPanel);
						mainPanel.add(checkoutPanel);
						mainPanel.add(roomsPanel);
						modifyButton = new JButton("Modify booking.");
						mainPanel.add(modifyButton);
						modifyButton.addActionListener(new ListenForButton());
						modifyButton.setBackground(new Color(37,183,203));
						mainPanel.add(new JLabel("Modification is subject to availability " +
							"rooms."));
						bookingPanel.add(cancelBooking);
					}
					hotelBox.add(bookingPanel);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// Add border
		hotelBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)); 

		// Add hotel panel to main panel
		hotelBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainBox.add(hotelBox);
		if(mainPanel != null)
		{
			frame.setSize(WIDTH, 650);
			mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)); 
			mainBox.add(mainPanel);
		}

		// Back Button
		backButton = new JButton("Back.");
		backButton.setToolTipText("Back to search page.");
		backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainBox.add(backButton);

		// Listeners
		ListenForButton buttonListener = new ListenForButton();
		backButton.addActionListener(buttonListener);

		// Colors
		backButton.setBackground(new Color(211,27,27));

		// Add rigid area for spacing between hotel panels
		mainBox.add(Box.createRigidArea(new Dimension	(0,5)));

		// Adds the panel to window and makes the window visible.
		frame.add(mainBox);
		frame.setVisible(true);
	}

	private class ListenForButton implements ActionListener
	{	
		private PreparedStatement pst = null;
		private java.util.Date checkinDate, checkoutDate;
		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == backButton)
			{
				new HotelDetailsPage(user);
				frame.setVisible(false);
				frame.dispose();
			}
			else if(event.getSource() == finaliseButton)
			{
				String booking = "INSERT INTO Bookings (username, hotelID,"+
				"roomCount ,checkinDate,checkoutDate,status,rated,pan)"
				+" VALUES(?,?,?,?,?,?,?,?);";
				String selectBooking = "SELECT last_insert_rowid() as rowid from"
				+" Bookings;";
				try
				{
					if(!Validater.validatePan(pan.getText()))
					{
						JOptionPane.showMessageDialog(frame,"PAN number is invalid.",
						"Error", JOptionPane.ERROR_MESSAGE);	
					}
					else
					{
						pst = conn.prepareStatement(booking);
						pst.setString(1,user.username);
						pst.setInt(2,h.hotelID);
						pst.setDouble(4,user.search.checkinDate.getTime());
						pst.setDouble(5,user.search.checkoutDate.getTime());
						pst.setInt(7,0);
						pst.setString(8,pan.getText());
						if(user.search.rooms <= h.freeRooms)
						{
							pst.setInt(3,user.search.rooms);
							pst.setInt(6,1);
						}
						else 
						{
							pst.setInt(3,user.search.rooms);
							pst.setInt(6,2);
						}
						pst.executeUpdate();
						pst = conn.prepareStatement(selectBooking);
						ResultSet rs = pst.executeQuery();
						int bookingRef = rs.getInt("rowid");
						user.updateBooking(bookingRef);
						JOptionPane.showMessageDialog(frame,"Your booking reference number is "
						+ bookingRef,
						"Registration Successful.", JOptionPane.INFORMATION_MESSAGE);
						new BookingPage(user,h);
						frame.setVisible(false);
						frame.dispose();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(event.getSource() == cancelWait)
			{
				String cancel = "UPDATE Bookings SET status = 0 WHERE rowid =?;";
				try
				{
					pst = conn.prepareStatement(cancel);
					pst.setInt(1,user.bookingRef);
					pst.executeUpdate();
					JOptionPane.showMessageDialog(frame,"Your waitlisting has been cancelled.",
						"Registration Successful.", JOptionPane.INFORMATION_MESSAGE);
					new BookingPage(user,h);
					frame.setVisible(false);
					frame.dispose();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(event.getSource() == cancelBooking)
			{
				cancel(user.bookingRef);
				JOptionPane.showMessageDialog(frame,"Your booking has been cancelled.",
				"Registration Successful.", JOptionPane.INFORMATION_MESSAGE);
				new BookingPage(user,h);
				frame.setVisible(false);
				frame.dispose();
			}
			else if(event.getSource() == modifyButton)
			{
				modify();
			}
		}
	}

	private void howToListenForChanges(JSCRater rater, Hotel h) 
	{
		rater.addRatingChangeListener(new RatingChangeListener() 
		{
			@Override
			public void ratingChanged(RatingChangeEvent ratingChangeEvent) 
			{
				String check = "SELECT rowid, * FROM Bookings WHERE rowid =?;";
				String update = "UPDATE Bookings SET rated = ? WHERE rowid =?;";
				try
				{
					PreparedStatement pst = conn.prepareStatement(check);
					pst.setInt(1,user.bookingRef);
					ResultSet rs = pst.executeQuery();
	
					if(rs.next() && rs.getInt("hotelID") == h.hotelID && rs.getInt("status") == 3
						&& rs.getInt("rated") == 0 && 
						daysFromXtoY(new java.util.Date(rs.getLong("checkoutDate")),
						new java.util.Date()) >= 0)
					{
						JSCRater rater = ratingChangeEvent.getSource();
						BigDecimal oldValue = ratingChangeEvent.getOldValue();
						BigDecimal newValue = ratingChangeEvent.getNewValue();
						double newRating = (h.noOfRatings*h.rating + newValue.doubleValue())/(++h.noOfRatings);
						h.rating = newRating;
						h.updateRating();
						pst = conn.prepareStatement(update);
						pst.setInt(1,1);
						pst.setInt(2,user.bookingRef);
						pst.executeUpdate();
					}
					else
					{
						JOptionPane.showMessageDialog(frame,"You can only rate a hotel if the "
						+ "following conditions are met: It was your last booking, " 
						+ " it is past your checkout date, you haven't rated it before.", 
						"Error",
						 JOptionPane.ERROR_MESSAGE);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void howToChangeThePainters(JSCRater rater)
	{
		//create the unselected painter
		// Gold
		SolidColorPainter unselectedPainter = new SolidColorPainter(new Color(255,204,51));
		
		//create the selected
		// Dark Yellow and Orange
		GradientColorPainter selectedPainter = new GradientColorPainter(new Color(255,204,0),
				 new Color(255,102,0));
		
		//set the painters
		rater.setUnselectedPainter(unselectedPainter);
		rater.setSelectedPainter(selectedPainter);
	}

	private void cancel(int bookingRef)
	{
		String cancel = "UPDATE Bookings SET status = 0 WHERE rowid =?;";
		String select = "SELECT * FROM Bookings WHERE rowid =?;";
		String overlappingBookings = "SELECT rowid, * FROM Bookings WHERE hotelID = ?" +
			" AND checkinDate <= ? AND checkoutDate >= ? AND status = 2;";
		try
		{
			PreparedStatement pst = conn.prepareStatement(select);
			pst.setInt(1,bookingRef);
			ResultSet rs = pst.executeQuery();
			h.freeRooms += rs.getInt("roomCount");
			long checkoutDate = rs.getLong("checkoutDate");
			long checkinDate = rs.getLong("checkinDate");
			long daysTill = Portal.daysFromXtoY(new java.util.Date(), 
				new Date(checkinDate));
			pst = conn.prepareStatement(cancel);
			pst.setInt(1,bookingRef);
			pst.executeUpdate();
			String overlappingWaitlist = "SELECT rowid, * FROM Bookings WHERE hotelID = ?" 
			+ " AND checkinDate <= ? AND checkoutDate >= ? AND status = 2;";
			pst = conn.prepareStatement(overlappingWaitlist);
			pst.setInt(1,h.hotelID);
			pst.setLong(2, checkoutDate);
			pst.setLong(3, checkinDate);
			rs = pst.executeQuery();
			while(h.freeRooms > 0 && rs.next())
			{
				if(rs.getInt("roomCount") <= h.freeRooms)
				{
					h.freeRooms -= rs.getInt("roomCount");
					String updateWait  = "UPDATE Bookings SET status=1 WHERE rowid=?";
					PreparedStatement pst1 = conn.prepareStatement(updateWait);
					pst1.setInt(1,rs.getInt("rowid"));
					pst1.executeUpdate();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}	
	}

	private void modify()
	{
		boolean valid = true;
				try
				{
					java.util.Date checkoutDate = DateFormat.getDateInstance().parse(
					checkout.getText());
					java.util.Date checkinDate = DateFormat.getDateInstance().parse(
					checkin.getText());
					java.util.Date today = new java.util.Date();
					long daysBetween;
					long daysFromToday;
					daysBetween = daysFromXtoY(checkinDate, checkoutDate);
					daysFromToday = daysFromXtoY(today, checkinDate);
					if(daysFromToday < 1)
					{
						JOptionPane.showMessageDialog(frame,"Booking must be done atleast "
							+ "one day in advance.", "Error", 
						JOptionPane.ERROR_MESSAGE);	
						valid = false;
					}
					else if(daysBetween <= 0)
					{
						JOptionPane.showMessageDialog(frame,"Check-out date must be at" 
						+ " least one day after check-in date.", "Error", 
						JOptionPane.ERROR_MESSAGE);	
						valid = false;
					} 
					if(valid)
					{
						String overlappingBookings = "SELECT rowid, * FROM Bookings where hotelID = ? AND"
		+" checkinDate <= ? AND checkoutDate >= ? AND status = 1;";
						PreparedStatement pst = conn.prepareStatement(overlappingBookings);
						pst.setInt(1,h.hotelID);
						pst.setDouble(2,checkoutDate.getTime());
						pst.setDouble(3,checkinDate.getTime());
						ResultSet rs = pst.executeQuery();
						int bookedRooms = 0;
						while(rs.next())
						{
							if(rs.getInt("rowid") != user.bookingRef)
							{
								bookedRooms += rs.getInt("roomCount");
							}
						}
						int freeRooms = h.roomCount - bookedRooms;
						if(freeRooms < rooms.getValue())
						{
							JOptionPane.showMessageDialog(frame,"Not enough rooms "
								+ "available.", "Error", 
						JOptionPane.ERROR_MESSAGE);
						}
						else
						{
							int oldBookingRef = user.bookingRef;
							String newBooking = "INSERT INTO Bookings (username, hotelID,"+
							"roomCount ,checkinDate,checkoutDate,status,rated,pan)"
							+" VALUES(?,?,?,?,?,?,?,?);";
							String getPan = "SELECT * FROM Bookings WHERE rowid =?;";
							pst = conn.prepareStatement(getPan);
							pst.setInt(1,user.bookingRef);
							rs = pst.executeQuery();
							String panNumber = rs.getString("pan");
							pst = conn.prepareStatement(newBooking);
							pst.setString(1, user.username);
							pst.setInt(2,h.hotelID);
							pst.setInt(3,rooms.getValue());
							pst.setLong(4, checkinDate.getTime()); 
							pst.setLong(5,checkoutDate.getTime());
							pst.setInt(6,1);
							pst.setInt(7,0);
							pst.setString(8,panNumber);
							pst.executeUpdate();
							cancel(oldBookingRef);
							String selectBooking = "SELECT last_insert_rowid() as rowid from"
				+" Bookings;";
							pst = conn.prepareStatement(selectBooking);
							rs = pst.executeQuery();
							int bookingRef = rs.getInt("rowid");
							user.search.checkinDate = checkinDate;
							user.search.checkoutDate = checkoutDate;
							user.updateBooking(bookingRef);
							JOptionPane.showMessageDialog(frame,"Your booking has been modified.",
						"Modification Successful.", JOptionPane.INFORMATION_MESSAGE);
							new BookingPage(user,h);
							frame.setVisible(false);
							frame.dispose();
						}
					}
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(frame,"Both dates need to be picked", "Error", 
					JOptionPane.ERROR_MESSAGE);	
				}
	}
}