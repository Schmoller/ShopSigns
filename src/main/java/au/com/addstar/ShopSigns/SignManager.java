package au.com.addstar.ShopSigns;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import au.com.addstar.ShopSigns.buy.BuySignDefinition;

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
				System.out.println("Attempting to use " + def.getClass());
				if(def.initialize(event))
					return true;
			}
			catch(IllegalArgumentException e)
			{
				event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
			}
			catch(IllegalAccessException e)
			{
				System.out.println("No permission");
				return true;
			}
		}
		
		return false;
	}
	
	private void loadDefaultDefinitions()
	{
		addDefinition(new BuySignDefinition());
	}
}
