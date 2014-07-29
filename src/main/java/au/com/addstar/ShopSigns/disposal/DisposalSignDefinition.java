package au.com.addstar.ShopSigns.disposal;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.SignDefinition;

public class DisposalSignDefinition extends SignDefinition
{
	@Override
	public DisposalSign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Disposal]"))
			return null;
		
		return new DisposalSign(sign.getLocation());
	}

	@Override
	public boolean initialize( SignChangeEvent event ) throws IllegalArgumentException, IllegalAccessException
	{
		if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[disposal]"))
			return false;
		
		if(!event.getPlayer().hasPermission("shopsigns.disposal.create"))
			throw new IllegalAccessException();
		
		event.setLine(0, ChatColor.DARK_BLUE + "[Disposal]");
		return true;
	}

}
