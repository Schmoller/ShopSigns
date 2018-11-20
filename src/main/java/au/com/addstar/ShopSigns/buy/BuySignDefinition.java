package au.com.addstar.ShopSigns.buy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.monolith.lookup.Lookup;

public class BuySignDefinition extends SignDefinition
{
	@Override
	public BuySign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Buy]"))
			return null;
		
		return new BuySign(sign.getLocation(), parseItem(sign.getLine(2)), parseCount(sign.getLine(1)), parsePrice(sign.getLine(3)));
	}

	@Override
	public boolean initialize( SignChangeEvent event ) throws IllegalArgumentException, IllegalAccessException
	{
		if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[buy]"))
			return false;
		
		if(!event.getPlayer().hasPermission("shopsigns.buy.create"))
			throw new IllegalAccessException();
		
		parseCount(event.getLine(1));
		parseItem(event.getLine(2));
		parsePrice(event.getLine(3));
		
		event.setLine(0, ChatColor.DARK_BLUE + "[Buy]");
		return true;
	}
	
	private ItemStack parseItem(String line)
	{
		String[] parts = line.split(":");
		Material type;
		try
		{
			type  = Material.getMaterial(parts[0].toUpperCase());
		}
		catch(NumberFormatException e)
		{
			type = Lookup.findItemByName(parts[0]);
		}
		
		if(type == null)
			throw new IllegalArgumentException("Line 2: Unknown item " + parts[0]);
		

		
		return new ItemStack(type,1);
	}
	
	private double parsePrice(String line) throws IllegalArgumentException
	{
		if(line.startsWith("$"))
			line = line.substring(1);
		
		try
		{
			double amount = Double.parseDouble(line);
			if(amount < 0)
				throw new IllegalArgumentException("Line 4: Price must be 0 or more");
			
			return amount;
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Line 4: Price must be a decimal 0 or more. It may start with $");
		}
	}

	public int parseCount(String line) throws IllegalArgumentException
	{
		try
		{
			int count = Integer.parseInt(line);
			if(count <= 0)
				throw new IllegalArgumentException("Line 2: Count must be 1 or higher");
			
			return count;
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Line 2: Count must be a number 1 or higher");
		}
	}
}
