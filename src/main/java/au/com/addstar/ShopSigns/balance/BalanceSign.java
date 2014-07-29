package au.com.addstar.ShopSigns.balance;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;

public class BalanceSign extends InteractiveSign
{
	public BalanceSign(Location location)
	{
		super(location);
	}
	
	@Override
	public void onLeftClick( Player player, ItemStack item )
	{
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(!player.hasPermission("shopsigns.balance.use"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to use that");
			return;
		}
		
		player.sendMessage(ChatColor.GOLD + "You have " + ShopSignsPlugin.getEconomy().format(ShopSignsPlugin.getEconomy().getBalance(player)));
	}

}
