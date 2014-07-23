package au.com.addstar.ShopSigns;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public abstract class SignDefinition
{
	/**
	 * Parses an existing sign and returns an instance of InteractiveSign
	 * @param sign The sign to parse
	 * @return 
	 * @throws IllegalArgumentException Thrown if the sign format is incorrect
	 */
	public abstract InteractiveSign parse(Sign sign) throws IllegalArgumentException;
	
	public boolean isValid(Sign sign)
	{
		try
		{
			return (parse(sign) != null);
		}
		catch(IllegalArgumentException e)
		{
			return false;
		}
	}
	
	/**
	 * Initializes the sign. This may operate on the sign changing it into the correct format. This should NOT do anything if the type is not correct
	 * @param event The event called to initialize the sign
	 * @return True if the sign was initialize, false if it is completely not one of these signs
	 * @throws IllegalArgumentException Thrown when something in the format does not match
	 * @throws IllegalAccessException Thrown when the player cannot create the sign
	 */
	public abstract boolean initialize(SignChangeEvent event) throws IllegalArgumentException, IllegalAccessException;
}
