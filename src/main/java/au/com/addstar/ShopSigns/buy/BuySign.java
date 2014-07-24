package au.com.addstar.ShopSigns.buy;

import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;

public class BuySign extends InteractiveSign
{
	private ItemStack mItem;
	private int mAmount;
	private double mPrice;
	
	public BuySign(ItemStack item, int count, double price)
	{
		mItem = item;
		mAmount = count;
		mPrice = price;
	}
	
	@Override
	public void onLeftClick( Player player, ItemStack item )
	{
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(!player.hasPermission("shopsigns.buy.use"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to buy from here");
			return;
		}
		
		Economy econ = ShopSignsPlugin.getEconomy();
		if(econ.has(player, mPrice))
			doTransaction(player, mAmount);
		else
		{
			double unitPrice = mPrice / mAmount;
			int count = (int)(econ.getBalance(player) / unitPrice);
			if(count == 0)
				player.sendMessage(ChatColor.RED + "You have insufficient funds to purchase that");
			else
				doTransaction(player, count);
		}
	}
	
	@SuppressWarnings( "deprecation" )
	private void doTransaction(Player player, int count)
	{
		ItemStack toGive = new ItemStack(mItem);
		toGive.setAmount(count);
		
		Map<Integer, ItemStack> result = player.getInventory().addItem(toGive);
		if(result.isEmpty())
			completeTransaction(player, count);
		else
		{
			int toBuy = count - result.get(0).getAmount();
			if(toBuy == 0)
				player.sendMessage(ChatColor.RED + "Your inventory is full.");
			else
				completeTransaction(player, toBuy);
		}
		
		player.updateInventory();
	}
	
	private void completeTransaction(Player player, int count)
	{
		double price = calculatePrice(count);
		ShopSignsPlugin.getEconomy().withdrawPlayer(player, price);
		player.sendMessage(ChatColor.GREEN + "You have bought " + count + " " + mItem.getType() + "'s for " + ShopSignsPlugin.getEconomy().format(price));
	}
	
	private double calculatePrice(int count)
	{
		if(count == mAmount)
			return mPrice;
		
		double unitPrice = mPrice / mAmount;
		
		return unitPrice * count;
	}

}
