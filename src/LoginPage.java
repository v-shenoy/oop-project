import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends Portal
{
	private JTextField username;
	private JPasswordField password;
	private JButton loginButton, regButton;
	
	LoginPage()
	{
		super();
		setWindowProperties();
	}

	void setWindowProperties()
	{
		super.setWindowProperties();
		frame.setTitle("Login");

		// Creates a JPanel with GridBagLayout
		JPanel loginPanel = new JPanel(new GridBagLayout());

		/* The different components of the JPanel are added by changing 
		   the gridbag constraints to align them as needed. */
		JLabel usernameLabel = new JLabel("Username: ");
		username = new JTextField("Enter username.", 15);
		loginPanel.add(usernameLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		loginPanel.add(username, gridConstraints);

		JLabel passwordLabel = new JLabel("Password: ");
		password = new JPasswordField("", 15);
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 2;
		gridConstraints.gridwidth = 1;
		loginPanel.add(passwordLabel, gridConstraints);
		gridConstraints.gridx = 10;
		gridConstraints.gridwidth = 15;
		loginPanel.add(password, gridConstraints);
	
		loginButton = new JButton("Login.");
		gridConstraints.gridx = 20;
		gridConstraints.gridy = 10;
		gridConstraints.gridwidth = 1;
		loginPanel.add(loginButton, gridConstraints);

		JLabel regLabel = new JLabel("Don't have an account? ");
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 20;
		gridConstraints.gridwidth = 20;
		regButton = new JButton("Register.");
		regButton.setToolTipText("Create a new account to use the portal.");
		loginPanel.add(regLabel, gridConstraints);
		gridConstraints.gridx = 21;
		gridConstraints.gridwidth = 1;
		loginPanel.add(regButton, gridConstraints);

		// Adds listeners to buttons.
		ListenForButton buttonListener = new ListenForButton();
		loginButton.addActionListener(buttonListener);
		regButton.addActionListener(buttonListener);

		// Colors
		regButton.setBackground(new Color(37,183,203));
		loginButton.setBackground(new Color(60,203,37));

		// Adds the panel to window and makes the window visible.
		frame.add(loginPanel);
		frame.setVisible(true);
	}

	private class ListenForButton implements ActionListener
	{	
		private PreparedStatement pst = null;

		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == loginButton)
			{
				boolean loginSuccessful = false;
				try
				{
					String loginQuery = "SELECT * FROM Users WHERE username=\"" 
					+ username.getText() +"\";";
					pst = conn.prepareStatement(loginQuery);
					ResultSet rs = pst.executeQuery();
					if(rs.next())
					{
						String pwd = new String(password.getPassword());
						loginSuccessful = rs.getString("password").equals(pwd);
						if(loginSuccessful)
						{
							User user = new User(rs.getString("name"), rs.getString("username"),
								rs.getString("dob"), rs.getString("email"),
								rs.getString("address"), rs.getInt("bookingRef"));
							frame.setVisible(false);
							new SearchPage(user);
							frame.dispose();
						}
						else
						{
							JOptionPane.showMessageDialog(frame,"Username and password don't "
							+ "match.", "Incorrect Password", JOptionPane.ERROR_MESSAGE);
							password.setText("");
						}
					}
					else
					{
						JOptionPane.showMessageDialog(frame,"Username not found.",
						"Incorrect Username", JOptionPane.ERROR_MESSAGE);
						username.setText("");
						password.setText("");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(event.getSource() == regButton)
			{
				new RegPage();
				frame.setVisible(false);
				frame.dispose();
			}
		}
	}
}