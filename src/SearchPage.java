import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.*;
import org.jdatepicker.*;
import org.jdatepicker.util.*;
import org.jdatepicker.impl.*;
import java.util.*;	
import javax.swing.event.*;
import java.text.*;

public class SearchPage extends Portal
{
	private JTextField location;
	private JButton searchButton, logoutButton, pendingButton;
	private JSlider rooms, people;
	private User user;
	private JFormattedTextField checkin, checkout;

	SearchPage(User user)
	{
		super();
		this.user = user;
		setWindowProperties();
	}

	void setWindowProperties()
	{
		super.setWindowProperties();
		frame.setTitle("Search for hotels");

		// Creates a JPanel with GridBagLayout
		JPanel searchPanel = new JPanel(new BorderLayout());
		JPanel userPanel = new JPanel(new GridBagLayout());
		JPanel settingsPanel = new JPanel(new GridBagLayout());

		/* The different components of the user, search JPanels are added by changing 
		   the gridbag constraints to align them as needed. */
		JLabel greetings = new JLabel("Hello, " + user.name + ".");
		JLabel usernameInfo = new JLabel("Username: " + user.username); 
		JLabel emailInfo = new JLabel("Email: " + user.email);
		JLabel dobInfo = new JLabel("DOB: " + user.dob);
		
		gridConstraints.anchor = GridBagConstraints.CENTER;
		userPanel.add(greetings, gridConstraints);
		gridConstraints.gridy = 2;
		userPanel.add(usernameInfo, gridConstraints);
		gridConstraints.gridy = 3;
		userPanel.add(emailInfo, gridConstraints);
		gridConstraints.gridy = 4;
		userPanel.add(dobInfo, gridConstraints);
		gridConstraints.gridy = 6;
		try
		{
			String pendingQuery = "SELECT * FROM Bookings WHERE rowid=? AND " 
			+ "(status = 1 OR status = 2)";
			PreparedStatement pst = conn.prepareStatement(pendingQuery);
			pst.setInt(1,user.bookingRef);
			ResultSet rs = pst.executeQuery();
			if(rs.next())
			{
				pendingButton = new JButton("Pending request.");
				pendingButton.addActionListener(new ListenForButton());
				pendingButton.setBackground(new Color(37,183,203));
				userPanel.add(pendingButton, gridConstraints);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		userPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)); 
		searchPanel.add(userPanel, BorderLayout.NORTH);
		
		setConstraints();
		JLabel locationLabel = new JLabel("Location: ");
		location = new JTextField("Enter location.", 15);
		location.setToolTipText("Select location for the hotel.");
		settingsPanel.add(locationLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		settingsPanel.add(location, gridConstraints);

		JLabel checkinLabel = new JLabel("Check-In: ");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 2;
		settingsPanel.add(checkinLabel, gridConstraints);
		UtilDateModel model1 = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel1 = new JDatePanelImpl(model1, p);
		JDatePickerImpl checkinPicker = new JDatePickerImpl(datePanel1, new DateComponentFormatter());
		checkinPicker.setSize(5,5);
		checkin = checkinPicker.getJFormattedTextField();
		checkin.setToolTipText("Pick a check-in date.");
		gridConstraints.gridx = 8;
		gridConstraints.gridwidth = 18;
		settingsPanel.add(checkinPicker, gridConstraints);

		JLabel checkoutLabel = new JLabel("Check-Out: ");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 3;
		settingsPanel.add(checkoutLabel, gridConstraints);
		UtilDateModel model2 = new UtilDateModel();
		JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, p);
		JDatePickerImpl checkoutPicker = new JDatePickerImpl(datePanel2, new DateComponentFormatter());
		checkoutPicker.setSize(5,5);
		checkout = checkoutPicker.getJFormattedTextField();
		checkout.setToolTipText("Pick a check-out date.");
		gridConstraints.gridx = 8;
		gridConstraints.gridwidth = 18;
		settingsPanel.add(checkoutPicker, gridConstraints);

		JLabel roomsLabel = new JLabel("Rooms: ");
		rooms = new JSlider(1, 15, 1);
		rooms.setMinorTickSpacing(1);
		rooms.setMajorTickSpacing(2);
		rooms.setPaintTicks(true);
		rooms.setPaintLabels(true);
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 4;
		gridConstraints.gridwidth = 1;
		settingsPanel.add(roomsLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		settingsPanel.add(rooms, gridConstraints);
		

		JLabel peopleLabel = new JLabel("People: ");
		people = new JSlider(1, 30, 1);
		people.setMinorTickSpacing(1);
		people.setMajorTickSpacing(5);
		people.setPaintTicks(true);
		people.setPaintLabels(true);
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 6;
		gridConstraints.gridwidth = 1;
		settingsPanel.add(peopleLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		settingsPanel.add(people, gridConstraints);
	
		searchButton = new JButton("Search.");
		searchButton.setToolTipText("Click to initiate search.");
		gridConstraints.gridx = 20;
		gridConstraints.gridy = 10;
		gridConstraints.gridwidth = 1;
		settingsPanel.add(searchButton, gridConstraints);

		logoutButton = new JButton("Logout.");
		logoutButton.setToolTipText("Logout from the portal.");
		gridConstraints.gridx = 21;
		settingsPanel.add(logoutButton, gridConstraints);

		// Adds listeners to buttons.
		ListenForButton buttonListener = new ListenForButton();
		logoutButton.addActionListener(buttonListener);
		searchButton.addActionListener(buttonListener);
		
		// Adds listeners to sliders.
		ListenForSlider sliderListener = new ListenForSlider();
		rooms.addChangeListener(sliderListener);
		people.addChangeListener(sliderListener);

		// Add settings panel to search panel
		searchPanel.add(settingsPanel, BorderLayout.CENTER);
		
		// Colors
		searchButton.setBackground(new Color(60,203,37));
		logoutButton.setBackground(new Color(211,27,27));

		//Adds the panel to window and makes the window visible.
		frame.add(searchPanel);
		frame.setVisible(true);
	}

	private class ListenForButton implements ActionListener
	{	
		private PreparedStatement pst = null;
		private java.util.Date checkinDate, checkoutDate;

		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == logoutButton)
			{
				new LoginPage();
				frame.setVisible(false);
				frame.dispose();
			}
			else if(event.getSource() == searchButton)
			{
				boolean valid = validateSearch();

				if(valid)
				{
					String loc = location.getText();
					int r = rooms.getValue();
					int p = people.getValue();
					user.setQuery(loc, checkinDate, checkoutDate,r,p);
					new HotelDetailsPage(user);
					frame.setVisible(false);
					frame.dispose();
				}
			}
			else if(event.getSource() == pendingButton)
			{
				try
				{
					String pendingQuery = "SELECT * FROM Bookings WHERE rowid=? AND " 
					+ "(status = 1 OR status = 2)";
					PreparedStatement pst = conn.prepareStatement(pendingQuery);
					pst.setInt(1,user.bookingRef);
					ResultSet rs = pst.executeQuery();
					if(rs.next())
					{
						String getHotelID = "SELECT rowid,* FROM Hotels WHERE rowid=?";
						PreparedStatement pst1 = conn.prepareStatement(getHotelID);
						pst1.setInt(1, rs.getInt("hotelID"));
						ResultSet rs1 = pst1.executeQuery();
						Hotel h = new Hotel(rs1);
						user.setQuery(h.location, 
						new java.util.Date(rs.getLong("checkinDate")),
						new java.util.Date(rs.getLong("checkoutDate")),
						rs.getInt("roomCount"), 0);
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
		}

		private boolean validateSearch()
		{
			boolean valid = true;
			if(valid && Validater.validateLocation(location.getText()) == false)
			{
				JOptionPane.showMessageDialog(frame,"Enter a valid city name.", "Error",
				JOptionPane.ERROR_MESSAGE);	
				valid = false;
			}
	
			if(valid)
			{
				try 
				{
					checkoutDate = DateFormat.getDateInstance().parse(
					checkout.getText());
					checkinDate = DateFormat.getDateInstance().parse(
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
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(frame,"Both dates need to be picked", "Error", 
					JOptionPane.ERROR_MESSAGE);	
					valid = false;
				}
			}
	
			
			return valid;
		}
	}

	private class ListenForSlider implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent event)
		{
			JSlider source = (JSlider)event.getSource();
			if(!source.getValueIsAdjusting())
			{
				source.setToolTipText(Integer.toString(source.getValue()));
			}
		}
	}
}