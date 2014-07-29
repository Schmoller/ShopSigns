package au.com.addstar.ShopSigns.enchant;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.ShopSigns.Util;

public class EnchantSignDefinition extends SignDefinition
{
	private HashMap<String, Enchantment> mEnchantments = new HashMap<String, Enchantment>();
	
	public EnchantSignDefinition()
	{
		mEnchantments.put("projectile", Enchantment.PROTECTION_PROJECTILE);
		mEnchantments.put("fireprotection", Enchantment.PROTECTION_FIRE);
		mEnchantments.put("aquaaffinity", Enchantment.WATER_WORKER);
		mEnchantments.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
		mEnchantments.put("sharpness", Enchantment.DAMAGE_ALL);
		mEnchantments.put("falling", Enchantment.PROTECTION_FALL);
		mEnchantments.put("thorns", Enchantment.THORNS);
		mEnchantments.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
		mEnchantments.put("fireaspect", Enchantment.FIRE_ASPECT);
		mEnchantments.put("blast", Enchantment.PROTECTION_EXPLOSIONS);
		mEnchantments.put("damage", Enchantment.ARROW_DAMAGE);
		mEnchantments.put("power", Enchantment.ARROW_DAMAGE);
		mEnchantments.put("flame", Enchantment.ARROW_FIRE);
		mEnchantments.put("punch", Enchantment.ARROW_KNOCKBACK);
		mEnchantments.put("infinity", Enchantment.ARROW_INFINITE);
		mEnchantments.put("looting", Enchantment.LOOT_BONUS_MOBS);
		mEnchantments.put("respiration", Enchantment.OXYGEN);
		mEnchantments.put("efficiency", Enchantment.DIG_SPEED);
		mEnchantments.put("knockback", Enchantment.KNOCKBACK);
		mEnchantments.put("silktouch", Enchantment.SILK_TOUCH);
		mEnchantments.put("vsarthropods", Enchantment.DAMAGE_ARTHROPODS);
		mEnchantments.put("arthropods", Enchantment.DAMAGE_ARTHROPODS);
		mEnchantments.put("unbreaking", Enchantment.DURABILITY);
		mEnchantments.put("vsundead", Enchantment.DAMAGE_UNDEAD);
		mEnchantments.put("smite", Enchantment.DAMAGE_UNDEAD);
		mEnchantments.put("lure", Enchantment.LURE);
		mEnchantments.put("luckofsea", Enchantment.LUCK);
	}
	
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
	
	@SuppressWarnings( "deprecation" )
	private Enchantment parseEnchant(String line) throws IllegalArgumentException
	{
		Enchantment enchant = mEnchantments.get(line.toLowerCase());
		if(enchant == null)
			enchant = Enchantment.getByName(line.toUpperCase());
		if(enchant == null)
		{
			try
			{
				int id = Integer.parseInt(line);
				enchant = Enchantment.getById(id);
			}
			catch(NumberFormatException e)
			{
			}
		}
		
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
