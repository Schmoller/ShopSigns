package au.com.addstar.ShopSigns.disposal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;

public class DisposalSign extends InteractiveSign
{
	public DisposalSign(Location location)
	{
		super(location);
	}
	
	@Override
	public void onLeftClick( Player player, ItemStack item )
	{
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(!player.hasPermission("shopsigns.disposal.use"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to use that");
			return;
		}
		Inventory inventory = Bukkit.createInventory(player, 36, "Disposal");
		player.openInventory(inventory);
	}

}
