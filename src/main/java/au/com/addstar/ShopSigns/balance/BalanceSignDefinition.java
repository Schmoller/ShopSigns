package au.com.addstar.ShopSigns.balance;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.SignDefinition;

public class BalanceSignDefinition extends SignDefinition
{
	@Override
	public BalanceSign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Balance]"))
			return null;
		
		return new BalanceSign(sign.getLocation());
	}

	@Override
	public boolean initialize( SignChangeEvent event ) throws IllegalArgumentException, IllegalAccessException
	{
		if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[balance]"))
			return false;
		
		if(!event.getPlayer().hasPermission("shopsigns.balance.create"))
			throw new IllegalAccessException();
		
		event.setLine(0, ChatColor.DARK_BLUE + "[Balance]");
		return true;
	}

}
