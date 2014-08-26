package au.com.addstar.ShopSigns.buy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.monolith.lookup.Lookup;
import au.com.addstar.monolith.lookup.MaterialDefinition;

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
	
	@SuppressWarnings( "deprecation" )
	private ItemStack parseItem(String line)
	{
		String[] parts = line.split(":");
		MaterialDefinition def = null;
		
		try
		{
			int id = Integer.parseInt(parts[0]);
			def = new MaterialDefinition(Material.getMaterial(id), (short)-1);
		}
		catch(NumberFormatException e)
		{
			def = Lookup.findItemByName(parts[0]);
		}
		
		if(def == null)
		{
			Material material = Material.getMaterial(parts[0].toUpperCase());
			if(material == null)
				throw new IllegalArgumentException("Line 2: Unknown item " + parts[0]);
			
			def = new MaterialDefinition(material, (short)-1);
		}
		
		if(parts.length == 2 && def.getData() == -1)
		{
			try
			{
				short data = Short.parseShort(parts[1]);
				if(data < 0)
					throw new IllegalArgumentException("Line 3: Data value is out of range");
				
				def = new MaterialDefinition(def.getMaterial(), data);
			}
			catch(NumberFormatException e)
			{
				throw new IllegalArgumentException("Line 3: Data value can only be numbers. Names are not supported");
			}
		}
		else if (def.getData() == -1)
			def = new MaterialDefinition(def.getMaterial(), (short)0);
		
		return def.asItemStack(1);
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
