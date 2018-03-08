package au.com.addstar.ShopSigns;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class InteractiveSign
{
	private final Location mLocation;
	public InteractiveSign(Location location)
	{
		mLocation = location;
	}
	
	public Location getLocation()
	{
		return mLocation;
	}
	
	public abstract void onLeftClick(Player player, ItemStack item);
	public abstract void onRightClick(Player player, ItemStack item);
}
