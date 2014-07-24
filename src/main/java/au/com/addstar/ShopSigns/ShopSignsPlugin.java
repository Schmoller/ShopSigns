package au.com.addstar.ShopSigns;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopSignsPlugin extends JavaPlugin
{
	private static Economy mEconomy;
	
	private SignManager mManager;
	
	public static boolean allowPartialBuy;
	public static boolean allowPartialSell;
	public static long signCooldown;
	
	@Override
	public void onEnable()
	{
		RegisteredServiceProvider<Economy> econ = Bukkit.getServicesManager().getRegistration(Economy.class);
		if(econ == null)
			throw new IllegalStateException("Unknown economy provider");
		
		mEconomy = econ.getProvider();
		
		loadConfig();
		
		mManager = new SignManager();
		Bukkit.getPluginManager().registerEvents(new EventListener(mManager), this);
	}
	
	private void loadConfig()
	{
		saveDefaultConfig();
		double cooldown = getConfig().getDouble("cooldown", 0.5);
		signCooldown = (long)(cooldown * 1000);
		allowPartialBuy = getConfig().getBoolean("allow-partial-buy", true);
		allowPartialSell = getConfig().getBoolean("allow-partial-sell", true);
	}
	
	public static Economy getEconomy()
	{
		return mEconomy;
	}
}
