package au.com.addstar.ShopSigns.weather;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;

public class WeatherSign extends InteractiveSign
{
	private WeatherState mState;
	private double mPrice;
	
	public WeatherSign(WeatherState state, double price)
	{
		mState = state;
		mPrice = price;
	}
	
	@Override
	public void onLeftClick( Player player, ItemStack item )
	{
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(!player.hasPermission("shopsigns.weather.use"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to change the weather");
			return;
		}
		
		Economy econ = ShopSignsPlugin.getEconomy();
		if(mPrice == 0 || econ.has(player, mPrice))
		{
			switch(mState)
			{
			case Storm:
				player.getWorld().setThundering(true);
				player.getWorld().setThunderDuration(8000);
			case Rain:
				player.getWorld().setWeatherDuration(10000);
				break;
			case Sun:
				player.getWorld().setStorm(false);
				player.getWorld().setThundering(false);
				player.getWorld().setWeatherDuration(0);
				break;
			}
			
			player.sendMessage(ChatColor.GREEN + "You have changed the weather.");
			
			if(mPrice != 0)
			{
				econ.withdrawPlayer(player, mPrice);
				player.sendMessage(ChatColor.GREEN + econ.format(mPrice) + " has been taken from your balance.");
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You have insufficient funds to change the weather");
		}
	}

}
