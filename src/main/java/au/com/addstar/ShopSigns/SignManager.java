package au.com.addstar.ShopSigns;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.buy.BuySignDefinition;
import au.com.addstar.ShopSigns.disposal.DisposalSignDefinition;
import au.com.addstar.ShopSigns.enchant.EnchantSignDefinition;
import au.com.addstar.ShopSigns.sell.SellSignDefinition;
import au.com.addstar.ShopSigns.time.TimeSignDefinition;
import au.com.addstar.ShopSigns.weather.WeatherSignDefinition;

public class SignManager
{
	private ArrayList<SignDefinition> mDefinitions;
	
	public SignManager()
	{
		mDefinitions = new ArrayList<SignDefinition>();
		
		loadDefaultDefinitions();
	}
	
	public void addDefinition(SignDefinition definition)
	{
		mDefinitions.add(definition);
	}
	
	public InteractiveSign parseSign(Sign sign)
	{
		for(SignDefinition def : mDefinitions)
		{
			if(def.isValid(sign))
				return def.parse(sign);
		}
		
		return null;
	}
	
	public boolean initializeSign(SignChangeEvent event)
	{
		for(SignDefinition def : mDefinitions)
		{
			try
			{
				if(def.initialize(event))
					return true;
			}
			catch(IllegalArgumentException e)
			{
				event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
			}
			catch(IllegalAccessException e)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void loadDefaultDefinitions()
	{
		addDefinition(new BuySignDefinition());
		addDefinition(new SellSignDefinition());
		addDefinition(new TimeSignDefinition());
		addDefinition(new WeatherSignDefinition());
		addDefinition(new EnchantSignDefinition());
		addDefinition(new DisposalSignDefinition());
	}
}
