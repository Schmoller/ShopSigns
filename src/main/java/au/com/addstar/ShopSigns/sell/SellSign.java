package au.com.addstar.ShopSigns.sell;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;

public class SellSign extends InteractiveSign
{
	private ItemStack mItem;
	private int mCount;
	private double mPrice;
	
	public SellSign(ItemStack item, int count, double price)
	{
		mItem = item;
		mCount = count;
		mPrice = price;
	}
	
	@Override
	public void onLeftClick( Player player, ItemStack item )
	{
		if(!player.hasPermission("shopsigns.sell.use"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to sell here");
			return;
		}
		
		ItemStack toRemove = new ItemStack(mItem);
		toRemove.setAmount(mCount);
		
		if(!ShopSignsPlugin.allowPartialSell)
		{
			HashMap<Integer, ? extends ItemStack> items = player.getInventory().all(mItem.getType());
			int available = 0;
			for(ItemStack it : items.values())
			{
				if(it.isSimilar(mItem))
					available += it.getAmount();
			}
			
			if(available < mCount)
			{
				player.sendMessage(ChatColor.RED + "You do not have enough " + mItem.getType().name() + " to sell");
				return;
			}
		}
		
		Map<Integer, ItemStack> result = player.getInventory().removeItem(toRemove);
		int count = mCount;
		
		if(!result.isEmpty())
		{
			ItemStack leftover = result.get(0);
			count = mCount - leftover.getAmount();
		}
		
		if(count == 0)
			player.sendMessage(ChatColor.RED + "You do not have any " + mItem.getType().name() + " to sell");
		else
		{
			double money = (mPrice / mCount) * count;
			ShopSignsPlugin.getEconomy().depositPlayer(player, money);
			player.sendMessage(ChatColor.GREEN + "You sold " + count + " " + mItem.getType().name() + " for " + ShopSignsPlugin.getEconomy().format(money));
		}
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(player.hasPermission("shopsigns.sell.use"))
			player.sendMessage(ChatColor.GOLD + "To sell, left click the sign.");
	}

}
