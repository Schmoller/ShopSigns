package au.com.addstar.ShopSigns.weather;

import java.util.Random;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;
import au.com.addstar.ShopSigns.log.TradeLog;

public class WeatherSign extends InteractiveSign
{
	private WeatherState mState;
	private double mPrice;
	
	public WeatherSign(Location location, WeatherState state, double price)
	{
		super(location);
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
			int duration = (300 + new Random().nextInt(600)) * 20;
		    
			player.getWorld().setThunderDuration(duration);
			player.getWorld().setWeatherDuration(duration);
			
			switch(mState)
			{
			case Storm:
				player.getWorld().setThundering(true);
				player.getWorld().setStorm(true);
				break;
			case Rain:
				player.getWorld().setStorm(true);
				player.getWorld().setThundering(false);
				break;
			case Sun:
				player.getWorld().setStorm(false);
				player.getWorld().setThundering(false);
				break;
			}
			
			player.sendMessage(ChatColor.GREEN + "You have changed the weather to " + mState.name() + " for " + (duration / 20) + " seconds.");
			
			if(mPrice != 0)
			{
				econ.withdrawPlayer(player, mPrice);
				player.sendMessage(ChatColor.GREEN + econ.format(mPrice) + " has been taken from your balance.");
				TradeLog.log(player, "SET", "WEATHER", mPrice, 0, mState.name(), null, getLocation());
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You have insufficient funds to change the weather");
		}
	}

}
