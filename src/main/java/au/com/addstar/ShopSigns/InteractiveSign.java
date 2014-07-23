package au.com.addstar.ShopSigns;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class InteractiveSign
{
	public abstract void onLeftClick(Player player, ItemStack item);
	public abstract void onRightClick(Player player, ItemStack item);
}
