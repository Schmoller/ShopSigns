package au.com.addstar.ShopSigns;

import java.sql.SQLException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.addstar.ShopSigns.log.QuickshopListener;
import au.com.addstar.ShopSigns.log.TradeLog;

public class ShopSignsPlugin extends JavaPlugin
{
	private static Economy mEconomy;
	
	private SignManager mManager;
	private TradeLog mLog;
	
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
		
		if(mLog != null)
		{
			try
			{
				mLog.initialize();
			}
			catch(IllegalArgumentException e)
			{
				getLogger().severe("Could not connect to database: ");
				e.getCause().printStackTrace();
				mLog = null;
			}
		}
		
		mManager = new SignManager();
		Bukkit.getPluginManager().registerEvents(new EventListener(mManager), this);
		
		if(Bukkit.getPluginManager().isPluginEnabled("QuickShop"))
			Bukkit.getPluginManager().registerEvents(new QuickshopListener(), this);
	}
	
	@Override
	public void onDisable()
	{
		if(mLog != null)
		{
			try
			{
				mLog.shutdown();
			}
			catch(SQLException e)
			{
				getLogger().warning("An exception occured while shutting down the database connection:");
				e.printStackTrace();
			}
		}
	}
	
	private void loadConfig()
	{
		saveDefaultConfig();
		double cooldown = getConfig().getDouble("cooldown", 0.5);
		signCooldown = (long)(cooldown * 1000);
		allowPartialBuy = getConfig().getBoolean("allow-partial-buy", true);
		allowPartialSell = getConfig().getBoolean("allow-partial-sell", true);
		
		ConfigurationSection section = getConfig().getConfigurationSection("database");
		if(section != null)
			mLog = new TradeLog(section.getString("host"), section.getString("database"), section.getString("username"), section.getString("password"));
	}
	
	public static Economy getEconomy()
	{
		return mEconomy;
	}
}
