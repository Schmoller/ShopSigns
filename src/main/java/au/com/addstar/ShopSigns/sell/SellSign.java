package au.com.addstar.ShopSigns.sell;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;
import au.com.addstar.ShopSigns.log.TradeLog;
import au.com.addstar.monolith.StringTranslator;

public class SellSign extends InteractiveSign
{
	private ItemStack mItem;
	private int mCount;
	private double mPrice;
	
	public SellSign(Location location, ItemStack item, int count, double price)
	{
		super(location);
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
				player.sendMessage(ChatColor.RED + "You do not have enough " + StringTranslator.getName(mItem) + "'s to sell");
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
			player.sendMessage(ChatColor.RED + "You do not have any " + StringTranslator.getName(mItem) + "'s to sell");
		else
		{
			double money = (mPrice / mCount) * count;
			ShopSignsPlugin.getEconomy().depositPlayer(player, money);
			player.sendMessage(ChatColor.GREEN + "You sold " + count + " " + StringTranslator.getName(mItem) + "'s for " + ShopSignsPlugin.getEconomy().format(money));
			TradeLog.log(player, "SHOP", "SELL", money, count, mItem.getType().name(), String.valueOf(mItem.getDurability()), getLocation());
		}
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(player.hasPermission("shopsigns.sell.use"))
			player.sendMessage(ChatColor.GOLD + "To sell, left click the sign.");
	}

}
