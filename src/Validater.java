import java.util.*;
import java.text.*;
public class Validater
{
	// Checks for valid name 
	// Simple regex for simplicity, can be easily changed.
	public static boolean validateName(String name)
	{
		return !name.equals("");
	}

	// Username syntax is like a valid Java identifier.
	// Contains alphabets, numbers, underscore, and cannot being with number.
	public static boolean validateUsername(String username)
	{
		return username.matches("[A-Za-z_][A-Za-z_0-9]*");
	}

	// Checks for non-empty address
	public static boolean validateAddress(String address)
	{
		return !address.equals("");
	}

	// Checks for non-empty dob
	public static boolean validateDob(String dob)
	{
		if(dob.equals(""))
		{
			return false;
		}
		try
		{
			Date dobDate = DateFormat.getDateInstance().parse(dob);
			Date today = new java.util.Date();
			if(Portal.daysFromXtoY(dobDate, today) <= 0)
			{
				return false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	// Checks email against regex for validity
	// Regex pattern can be changed easily.
	public static boolean validateEmail(String email)
	{
		return email.matches("^\\w+[\\w-\\.]*\\@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}$");
	}

	// Greater than 8 characters, 1 digit, 1 upper case, 1 lower case, 1 special char.
	public static boolean validatePassword(String password)
	{
		return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
	}

	// Regex for checking if a location is valid
	public static boolean validateLocation(String location)
	{
		return location.matches("^[a-zA-Z]+(?:[ -][a-zA-Z]+)*$");
	}

	// Validation for PAN number
	public static boolean validatePan(String pan)
	{
		return pan.matches("[A-Z]{5}[0-9]{4}[A-Z]");
	}
}