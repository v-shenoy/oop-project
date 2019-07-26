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

public class HotelDetailsPage extends Portal
{
	private JTextField username;
	private JButton backButton, scrollButton;
	private JScrollPane scroller;
	private User user;
	private PreparedStatement pst;

	HotelDetailsPage(User user)
	{
		super();
		this.user = user;
		pst = null;
		setWindowProperties();
	}

	void setWindowProperties()
	{
		super.setWindowProperties();
		int WIDTH = 670;
		int HEIGHT = 500;
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle("Search results");

		// Create a JPanel with BoxLayout to display Hotels vertically
		Box mainBox = Box.createVerticalBox();
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		// Adds back button to back pane and add that to main panel.
		backButton = new JButton("Back.");
		backButton.setToolTipText("Back to search page.");
		mainBox.add(backButton);
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		String searchHotels = "SELECT rowid, * FROM Hotels where location = ? AND roomCount >=?"
		+ "ORDER BY rating DESC;";
		ResultSet rs;

		try
		{
			pst = conn.prepareStatement(searchHotels);
			pst.setString(1, user.search.location);
			pst.setInt(2, user.search.rooms);
			rs = pst.executeQuery();
			while(rs.next())
			{
				// Set hotel panel layout and sizes
				Hotel h = new Hotel(rs);
				Box hotelBox = Box.createVerticalBox();
				hotelBox.setPreferredSize(new Dimension(620,380));
				hotelBox.setMinimumSize(new Dimension(620,380));
				hotelBox.setMaximumSize(new Dimension(620,380));
				hotelBox.add(Box.createRigidArea(new Dimension(0,5)));

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
				for(File image : contents)
				{
					BufferedImage picture = ImageIO.read(image);
					JLabel hotelPic = new JLabel(new ImageIcon(picture.getScaledInstance(
					imageWIDTH, imageHEIGHT, Image.SCALE_FAST)));
					imageBox.add(hotelPic);
					imageBox.add(Box.createRigidArea(new Dimension(5,0)));
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

				// More info button with listener
				JButton infoButton = new JButton("View Info.");
				infoButton.setToolTipText("Click to view booking info");
				infoButton.addActionListener(new ListenForButton(h));
				hotelBox.add(infoButton);

				// Add border
				hotelBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

				// Add hotel panel to main panel
				hotelBox.setAlignmentX(Component.CENTER_ALIGNMENT);
				mainBox.add(hotelBox);

				// Colors
				infoButton.setBackground(new Color(60,203,37));

				// Add rigid area for spacing between hotel panels
				mainBox.add(Box.createRigidArea(new Dimension	(0,5)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

 		// Adds scroll button to main panel
		scrollButton = new JButton("Top.");
		scrollButton.setToolTipText("Go back to top.");
		mainBox.add(scrollButton);
		mainBox.add(Box.createRigidArea(new Dimension	(0,5)));

		// Adds listeners to buttons.
		ListenForButton buttonListener = new ListenForButton();
		backButton.addActionListener(buttonListener);
		scrollButton.addActionListener(buttonListener);

		// // Creates a scroll pane and binds it to main panel.
		scroller = new JScrollPane(mainBox,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setPreferredSize(new Dimension(600,500));
		scroller.getVerticalScrollBar().setUnitIncrement(20);

		// Colors
		scrollButton.setBackground(new Color(37,183,203));
		backButton.setBackground(new Color(211,27,27));

		// Adds scroll pane and mainpanel to the JFrame.
		frame.add(scroller);
		frame.setVisible(true);
	}

	private class ListenForButton implements ActionListener
	{
		private PreparedStatement pst = null;
		private Hotel h;

		ListenForButton(Hotel h)
		{
			this.h = h;
		}

		ListenForButton()
		{
			this.h = null;
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == backButton)
			{
				user.search = null;
				new SearchPage(user);
				frame.setVisible(false);
				frame.dispose();
			}
			else if(event.getSource() == scrollButton)
			{
				scroller.getVerticalScrollBar().setValue(0);
			}
			else
			{
				new BookingPage(user, this.h);
				frame.setVisible(false);
				frame.dispose();
			}
		}
	}

	private void howToListenForChanges(JSCRater rater, Hotel h)
	{
		PreparedStatement pst;
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

	private void howToChangeThePainters(JSCRater rater) {
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
}
