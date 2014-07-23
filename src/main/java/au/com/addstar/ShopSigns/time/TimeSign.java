package au.com.addstar.ShopSigns.time;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;

public class TimeSign extends InteractiveSign
{
	private int mTime;
	private double mPrice;
	
	public TimeSign(int time, double price)
	{
		mTime = time;
		mPrice = price;
	}
	
	@Override
	public void onLeftClick( Player player, ItemStack item )
	{
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(!player.hasPermission("shopsigns.time.use"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to change the time");
			return;
		}
		
		Economy econ = ShopSignsPlugin.getEconomy();
		if(mPrice == 0 || econ.has(player, mPrice))
		{
			player.getWorld().setTime(mTime);
			player.sendMessage(ChatColor.GREEN + "You have changed the time.");
			
			if(mPrice != 0)
			{
				econ.withdrawPlayer(player, mPrice);
				player.sendMessage(ChatColor.GREEN + econ.format(mPrice) + " has been taken from your balance.");
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You have insufficient funds to change the time");
		}
	}

}
