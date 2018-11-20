package au.com.addstar.ShopSigns.sell;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.ShopSigns.Util;
import au.com.addstar.monolith.lookup.Lookup;

public class SellSignDefinition extends SignDefinition
{
	@Override
	public SellSign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Sell]"))
			return null;
		
		return new SellSign(sign.getLocation(), parseItem(sign.getLine(2)), parseCount(sign.getLine(1)), Util.parsePrice(sign.getLine(3), 4));
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
	
	private ItemStack parseItem(String line)
	{
		String[] parts = line.split(":");
		Material def;
		def = Material.getMaterial(parts[0].toUpperCase());
		if(def == null)
			def = Lookup.findItemByName(parts[0]);
		if(def == null)
			throw new IllegalArgumentException("Line 2: Unknown item " + parts[0]);

		return new ItemStack(def,1);
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
