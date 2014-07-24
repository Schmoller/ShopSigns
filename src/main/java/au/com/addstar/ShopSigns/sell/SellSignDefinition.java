package au.com.addstar.ShopSigns.sell;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.ShopSigns.Util;

public class SellSignDefinition extends SignDefinition
{
	@Override
	public SellSign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Sell]"))
			return null;
		
		return new SellSign(parseItem(sign.getLine(2)), parseCount(sign.getLine(1)), Util.parsePrice(sign.getLine(3), 4));
	}

	@Override
	public boolean initialize( SignChangeEvent event ) throws IllegalArgumentException, IllegalAccessException
	{
		if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[sell]"))
			return false;
		
		if(!event.getPlayer().hasPermission("shopsigns.sell.create"))
			throw new IllegalAccessException();
		
		parseCount(event.getLine(1));
		parseItem(event.getLine(2));
		Util.parsePrice(event.getLine(3), 4);
		
		event.setLine(0, ChatColor.DARK_BLUE + "[Sell]");
		return true;
	}
	
	@SuppressWarnings( "deprecation" )
	private ItemStack parseItem(String line)
	{
		String[] parts = line.split(":");
		Material material = null;
		short data = 0;
		
		try
		{
			int id = Integer.parseInt(parts[0]);
			material = Material.getMaterial(id);
		}
		catch(NumberFormatException e)
		{
		}
		
		if(material == null)
		{
			material = Material.getMaterial(parts[0].toUpperCase());
			if(material == null)
				throw new IllegalArgumentException("Line 2: Unknown item " + parts[0]);
		}
		
		if(parts.length == 2)
		{
			try
			{
				data = Short.parseShort(parts[1]);
				if(data < 0)
					throw new IllegalArgumentException("Line 3: Data value is out of range");
			}
			catch(NumberFormatException e)
			{
				throw new IllegalArgumentException("Line 3: Data value can only be numbers. Names are not supported");
			}
		}
		
		return new ItemStack(material, 1, data);
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
