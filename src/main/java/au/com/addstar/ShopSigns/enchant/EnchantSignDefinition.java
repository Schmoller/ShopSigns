package au.com.addstar.ShopSigns.enchant;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.ShopSigns.Util;
import au.com.addstar.monolith.lookup.Lookup;

public class EnchantSignDefinition extends SignDefinition
{
	@Override
	public EnchantSign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.GOLD + "[Enchant]"))
			return null;
		
		Enchantment enchant = parseEnchant(sign.getLine(1));
		return new EnchantSign(sign.getLocation(), enchant, Util.parsePrice(sign.getLine(2), 3), parseMaxLevel(sign.getLine(3), enchant));
	}

	@Override
	public boolean initialize( SignChangeEvent event ) throws IllegalArgumentException, IllegalAccessException
	{
		if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[enchant]"))
			return false;
		
		if(!event.getPlayer().hasPermission("shopsigns.enchant.create"))
			throw new IllegalAccessException();
		
		Enchantment ench = parseEnchant(event.getLine(1));
		Util.parsePrice(event.getLine(2), 3);
		int level = parseMaxLevel(event.getLine(3), ench);
		
		event.setLine(0, ChatColor.GOLD + "[Enchant]");
		event.setLine(3, String.valueOf(level));
		
		return true;
	}
	
	private Enchantment parseEnchant(String line) throws IllegalArgumentException
	{
		Enchantment enchant = Lookup.findEnchantmentByName(line);
		if(enchant == null)
			enchant = Enchantment.getByKey(NamespacedKey.minecraft(line.toUpperCase()));
		if(enchant == null)
			enchant = Enchantment.getByName(line.toUpperCase());
		if(enchant == null)
			throw new IllegalArgumentException("Line 2: Unknown enchantment " + line);
		
		return enchant;
	}
	
	private int parseMaxLevel(String line, Enchantment enchant) throws IllegalArgumentException
	{
		if(line.isEmpty())
			return enchant.getMaxLevel();
		
		try
		{
			int level = Integer.parseInt(line);
			if(level <= 0 || level > 10)
				throw new IllegalArgumentException("Line 4: Maximum level must be between 1 and 10");
			
			return level;
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Line 4: Maximum level must be between 1 and 10, or left blank to use highest level for the enchant");
		}
	}

}
