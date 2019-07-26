import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.jdatepicker.*;
import org.jdatepicker.util.*;
import org.jdatepicker.impl.*;
import java.util.*;

public class RegPage extends Portal
{
	private JTextField name, username, email;
	private JFormattedTextField dob;
	private JTextArea address;
	private JPasswordField password, passwordTwo;
	private JButton loginButton, regButton;

	RegPage()
	{
		super();
		setWindowProperties();
	}

	void setWindowProperties()
	{
		super.setWindowProperties();
		frame.setTitle("Register");

		// Creates a JPanel with GridBagLayout
		JPanel regPanel = new JPanel(new GridBagLayout());

		/* The different components of the JPanel are added by changing 
		   the gridbag constraints to align them as needed. */
		JLabel nameLabel = new JLabel("Name: ");
		name = new JTextField("Enter name.", 15);
		regPanel.add(nameLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		regPanel.add(name, gridConstraints);

		JLabel usernameLabel = new JLabel("Username: ");
		username = new JTextField("Enter username.", 15);
		username.setToolTipText("Username must consist of only alphanumeric characters and _.");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 2;
		gridConstraints.gridwidth = 1;
		regPanel.add(usernameLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		regPanel.add(username, gridConstraints);

		JLabel emailLabel = new JLabel("Email-Id: ");
		email = new JTextField("Enter email.", 15);
		email.setToolTipText("Email must be of the form xyz@example.com.");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 3;
		gridConstraints.gridwidth = 1;
		regPanel.add(emailLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		regPanel.add(email, gridConstraints);

		JLabel addressLabel = new JLabel("Address: ");
		address = new JTextArea("Enter address.", 5, 15);
		address.setLineWrap(true);
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 4;
		gridConstraints.gridwidth = 1;
		regPanel.add(addressLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		gridConstraints.gridheight = 5;
		regPanel.add(address, gridConstraints);

		JLabel dobLabel = new JLabel("Date of birth: ");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 9;
		gridConstraints.gridwidth = 1;
		regPanel.add(dobLabel, gridConstraints);
		gridConstraints.gridx = 8;
		gridConstraints.gridwidth = 18;
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());
		datePicker.setSize(5,5);
		dob = datePicker.getJFormattedTextField();
		regPanel.add(datePicker, gridConstraints);


		JLabel passwordLabel = new JLabel("Password: ");
		password = new JPasswordField("", 15);
		password.setToolTipText("Password must be more than8 characters long, alphanumeric " 
			+ "and have at least one number.");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 20;
		gridConstraints.gridwidth = 1;
		regPanel.add(passwordLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		regPanel.add(password, gridConstraints);

		JLabel passwordLabelTwo = new JLabel("Re-enter password: ");
		passwordTwo = new JPasswordField("", 15);
		passwordTwo.setToolTipText("Passwords must match.");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 25;
		gridConstraints.gridwidth = 1;
		regPanel.add(passwordLabelTwo, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		regPanel.add(passwordTwo, gridConstraints);

		regButton = new JButton("Register.");
		regButton.setToolTipText("Create a new account to use the portal.");
		gridConstraints.gridx = 20;
		gridConstraints.gridy = 35;
		gridConstraints.gridwidth = 1;
		regPanel.add(regButton, gridConstraints);

		loginButton = new JButton("Go back.");
		loginButton.setToolTipText("Go to login page.");
		gridConstraints.gridx = 21;
		regPanel.add(loginButton, gridConstraints);

		// Adds listeners to buttons.
		ListenForButton buttonListener = new ListenForButton();
		regButton.addActionListener(buttonListener);
		loginButton.addActionListener(buttonListener);

		// Colors
		loginButton.setBackground(new Color(37,183,203));
		regButton.setBackground(new Color(60,203,37));

		// Adds the panel to window and makes the window visible.
		frame.add(regPanel);
		frame.setVisible(true);
	}

	private class ListenForButton implements ActionListener
	{
		private PreparedStatement pst = null;
		String pwd, pwdTwo;

		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == loginButton)
			{
				new LoginPage();
				frame.setVisible(false);
				frame.dispose();
			}
			else if(event.getSource() == regButton)
			{
				try 
				{
					String insertUser = "INSERT INTO Users (name,dob,"+
					"address,email,username,password, bookingRef) VALUES(?,?,?,?,?,?,?);";
				
					pwd = new String(password.getPassword());
					pwdTwo = new String(passwordTwo.getPassword());
					boolean valid = validateUser();
					
					if(valid)
					{
						pst = conn.prepareStatement(insertUser);
						pst.setString(1, name.getText());
						pst.setString(2, dob.getText());
						pst.setString(3, address.getText());
						pst.setString(4, email.getText());
						pst.setString(5, username.getText());
						pst.setString(6, pwd);
						pst.setInt(7,0);
						pst.executeUpdate();

						JOptionPane.showMessageDialog(frame,"You have been registered"
						+" successfully.",
						"Registration Successful.", JOptionPane.INFORMATION_MESSAGE);
						User user = new User(name.getText(), username.getText(), dob.getText(),
						email.getText(), address.getText(), 0);
						new SearchPage(user);
						frame.setVisible(false);
						frame.dispose();
					}
					if(pst != null)
					{
						pst.close();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		private boolean validateUser()
		{
			boolean valid = true;
			if(valid && Validater.validateName(name.getText()) == false)
			{
				JOptionPane.showMessageDialog(frame,"Not a valid name.", "Error",
				JOptionPane.ERROR_MESSAGE);	
				valid = false;
			}

			if(valid && Validater.validateUsername(username.getText()) == false)
			{
				JOptionPane.showMessageDialog(frame,"Username must consist of only"
				+ " alphanumeric characters, _ and must not begin with a number.", "Error",
				JOptionPane.ERROR_MESSAGE);	
				valid = false;	
			}
			else if(valid && Validater.validateUsername(username.getText()) == true)
			{
				try
				{
					String userQuery = "SELECT * FROM Users WHERE username=\"" 
					+ username.getText() +"\";";
					pst = conn.prepareStatement(userQuery);
					ResultSet rs = pst.executeQuery();
					if(rs.next())
					{
						JOptionPane.showMessageDialog(frame,"Username is not available.",
						"Error", JOptionPane.ERROR_MESSAGE);	
						valid = false;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(valid && Validater.validateEmail(email.getText()) == false)
			{
				JOptionPane.showMessageDialog(frame,"Invalid Email-Id.", "Error",
				JOptionPane.ERROR_MESSAGE);	
				valid = false;	
			}
			else if(valid && Validater.validateEmail(email.getText()) == true)
			{
				try
				{
					String userQuery = "SELECT * FROM Users WHERE email=\"" 
					+ email.getText() +"\";";
					pst = conn.prepareStatement(userQuery);
					ResultSet rs = pst.executeQuery();
					if(rs.next())
					{
						JOptionPane.showMessageDialog(frame,"Another account with this"
						+ " email exists.", "Error", JOptionPane.ERROR_MESSAGE);	
						valid = false;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(valid && Validater.validateAddress(address.getText()) == false)
			{
				JOptionPane.showMessageDialog(frame,"Address can't be empty.", "Error",
				JOptionPane.ERROR_MESSAGE);
				valid = false;
			}
			if(valid && Validater.validateDob(dob.getText()) == false)
			{
				JOptionPane.showMessageDialog(frame,"Invalid date of birth.", "Error",
				JOptionPane.ERROR_MESSAGE);
				valid = false;
			}
			if(valid && Validater.validatePassword(pwd) == false)
			{
				JOptionPane.showMessageDialog(frame,"Password must be at least" 
				+ "8 characters long, contain at least one digit, one lower-case,"
				+ " one upper-case, and one special character.", "Error",
				JOptionPane.ERROR_MESSAGE);	
				valid = false;
			}
			else if(valid && pwd.equals(pwdTwo) == false)
			{
				JOptionPane.showMessageDialog(frame,"Passwords don't match.", "Error",
				JOptionPane.ERROR_MESSAGE);	
				valid = false;
			}
			return valid;
		}
	}
}