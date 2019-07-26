import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class Portal
{
	JFrame frame;
	Connection conn;
	GridBagConstraints gridConstraints;

	Portal()
	{
		setUIFont(new javax.swing.plaf.FontUIResource("Serif",Font.BOLD,12));
		frame = new JFrame();
		gridConstraints = new GridBagConstraints();
		conn = DatabaseConnection.connect();
		setConstraints();
	}

	void setWindowProperties()
	{
		int WIDTH = 500;
		int HEIGHT = 400;
		
		// Sets the size, title of the JFrame.
		frame.setSize(WIDTH, HEIGHT);

		// Centers the JFrame relative to the screen.
		frame.setLocationRelativeTo(null);
		
		// Sets default closing operation of the frame.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		// Prevents the user from resizing window.
		frame.setResizable(false);
	}

	void setConstraints()
	{
		// Default gridbag constraints
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridheight = 1;
		gridConstraints.weightx = 0;
		gridConstraints.weighty = 0;
		gridConstraints.insets = new Insets(2,2,2,2);
		gridConstraints.anchor = GridBagConstraints.EAST;
		gridConstraints.fill = GridBagConstraints.NONE;
	}

	public static void main(String args[])
	{
		new LoginPage();
	}

	// Returns the number of days between 2 date objects
	public static long daysFromXtoY(java.util.Date X, java.util.Date Y)
	{
		return (Y.getTime() - X.getTime())/(1000*60*60*24);
	}

	// Sets the default font for every JComponent
	static void setUIFont (javax.swing.plaf.FontUIResource f)
	{
	    java.util.Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements())
	    {
	      	Object key = keys.nextElement();
	      	Object value = UIManager.get (key);
	      	if (value instanceof javax.swing.plaf.FontUIResource)
	      	{
	      		UIManager.put (key, f);
	    	}
	    }	
    } 
}