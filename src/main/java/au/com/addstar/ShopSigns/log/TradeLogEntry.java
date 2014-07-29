package au.com.addstar.ShopSigns.log;

import org.bukkit.Location;

public class TradeLogEntry
{
	public final long date;
	public final String type;
	public final String action;
	
	public final String player;
	public final String otherPlayer;
	
	public final double money;
	public final int quantity;
	
	public final String data;
	public final String extra;
	
	public final Location location;
	
	public TradeLogEntry(String player, String type, String action, double money, int quantity, String data, String extra, Location location)
	{
		this(player, null, type, action, money, quantity, data, extra, location);
	}
	
	public TradeLogEntry(String player, String otherPlayer, String type, String action, double money, int quantity, String data, String extra, Location location)
	{
		this.date = System.currentTimeMillis();
		
		this.player = player;
		this.otherPlayer = otherPlayer;
		this.type = type;
		this.action = action;
		this.money = money;
		this.quantity = quantity;
		this.data = data;
		this.extra = extra;
		this.location = location;
	}
}
