package au.com.addstar.ShopSigns;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopSignsPlugin extends JavaPlugin
{
	private static Economy mEconomy;
	
	private SignManager mManager;
	
	@Override
	public void onEnable()
	{
		RegisteredServiceProvider<Economy> econ = Bukkit.getServicesManager().getRegistration(Economy.class);
		if(econ == null)
			throw new IllegalStateException("Unknown economy provider");
		
		mEconomy = econ.getProvider();
		
		mManager = new SignManager();
		Bukkit.getPluginManager().registerEvents(new EventListener(mManager), this);
	}
	
	public static Economy getEconomy()
	{
		return mEconomy;
	}
}
