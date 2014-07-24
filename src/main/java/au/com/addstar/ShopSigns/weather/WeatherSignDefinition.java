package au.com.addstar.ShopSigns.weather;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.SignDefinition;
import au.com.addstar.ShopSigns.Util;

public class WeatherSignDefinition extends SignDefinition
{
	@Override
	public WeatherSign parse( Sign sign ) throws IllegalArgumentException
	{
		if(!sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Weather]"))
			return null;
		
		double price = 0;
		if(!sign.getLine(2).isEmpty())
			price = Util.parsePrice(sign.getLine(2), 3);
		
		return new WeatherSign(parseWeather(sign.getLine(1)), price);
	}

	@Override
	public boolean initialize( SignChangeEvent event ) throws IllegalArgumentException, IllegalAccessException
	{
		if(!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[Weather]"))
			return false;
		
		if(!event.getPlayer().hasPermission("shopsigns.weather.create"))
			throw new IllegalAccessException();
		
		parseWeather(event.getLine(1));
		if(!event.getLine(2).isEmpty())
			Util.parsePrice(event.getLine(2), 3);
		
		event.setLine(0, ChatColor.DARK_BLUE + "[Weather]");
		event.setLine(1, ChatColor.DARK_GREEN + ChatColor.stripColor(event.getLine(1)));
		
		return true;
	}
	
	private WeatherState parseWeather(String line) throws IllegalArgumentException
	{
		line = ChatColor.stripColor(line);
		
		for(WeatherState state : WeatherState.values())
		{
			if(state.name().equalsIgnoreCase(line))
				return state;
		}
			
		throw new IllegalArgumentException("Line 2: Unknown weather " + line + ". Valid values are Sun, Rain, Storm");
	}
}
