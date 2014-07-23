package au.com.addstar.ShopSigns.time;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.ShopSigns.Util;

public class TimeSignDefinition extends SignDefinition
{
	@Override
	public TimeSign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Time]"))
			return null;
		
		double price = 0;
		if(!sign.getLine(2).isEmpty())
			price = Util.parsePrice(sign.getLine(2), 3);
		
		return new TimeSign(parseTime(sign.getLine(1)), price);
	}

	@Override
	public boolean initialize( SignChangeEvent event ) throws IllegalArgumentException, IllegalAccessException
	{
		if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[time]"))
			return false;
		
		if(!event.getPlayer().hasPermission("shopsigns.time.create"))
			throw new IllegalAccessException();
		
		parseTime(event.getLine(1));
		if(!event.getLine(2).isEmpty())
			Util.parsePrice(event.getLine(2), 3);
		
		event.setLine(0, ChatColor.DARK_BLUE + "[Time]");
		event.setLine(1, ChatColor.DARK_GREEN + ChatColor.stripColor(event.getLine(1)));
		
		return true;
	}
	
	private int parseTime(String line) throws IllegalArgumentException
	{
		line = ChatColor.stripColor(line);
		
		if(line.equalsIgnoreCase("day"))
			return 0;
		else if(line.equalsIgnoreCase("night"))
			return 12000;
		else if(line.equalsIgnoreCase("noon"))
			return 6000;
		else if(line.equalsIgnoreCase("midnight"))
			return 18000;
		
		throw new IllegalArgumentException("Line 2: Unknown time " + line + ". Valid values are Day, Night, Noon, Midnight");
	}
}
