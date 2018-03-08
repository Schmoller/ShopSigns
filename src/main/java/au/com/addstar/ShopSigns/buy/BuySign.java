package au.com.addstar.ShopSigns.buy;

import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;
import au.com.addstar.ShopSigns.log.TradeLog;
import au.com.addstar.monolith.StringTranslator;

public class BuySign extends InteractiveSign
{
	private final ItemStack mItem;
	private final int mAmount;
	private final double mPrice;
	
	public BuySign(Location location, ItemStack item, int count, double price)
	{
		super(location);
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
		else if(ShopSignsPlugin.allowPartialBuy)
		{
			double unitPrice = mPrice / mAmount;
			int count = (int)(econ.getBalance(player) / unitPrice);
			if(count == 0)
				player.sendMessage(ChatColor.RED + "You have insufficient funds to purchase that");
			else
				doTransaction(player, count);
		}
		else
			player.sendMessage(ChatColor.RED + "You have insufficient funds to purchase that");
	}
	
	@SuppressWarnings( "deprecation" )
	private void doTransaction(Player player, int count)
	{
		ItemStack toGive = new ItemStack(mItem);
		toGive.setAmount(count);
		
		Map<Integer, ItemStack> result = player.getInventory().addItem(toGive);
		if(result.isEmpty())
			completeTransaction(player, count);
		else if(ShopSignsPlugin.allowPartialBuy)
		{
			int toBuy = count - result.get(0).getAmount();
			if(toBuy == 0)
				player.sendMessage(ChatColor.RED + "Your inventory is full.");
			else
				completeTransaction(player, toBuy);
		}
		else
			player.sendMessage(ChatColor.RED + "Your inventory is full.");
		
		player.updateInventory();
	}
	
	private void completeTransaction(Player player, int count)
	{
		double price = calculatePrice(count);
		ShopSignsPlugin.getEconomy().withdrawPlayer(player, price);
		player.sendMessage(ChatColor.GREEN + "You have bought " + count + " " + StringTranslator.getName(mItem) + "'s for " + ShopSignsPlugin.getEconomy().format(price));
		TradeLog.log(player, "SHOP", "BUY", price, count, mItem.getType().name(), String.valueOf(mItem.getDurability()), getLocation());
	}
	
	private double calculatePrice(int count)
	{
		if(count == mAmount)
			return mPrice;
		
		double unitPrice = mPrice / mAmount;
		
		return unitPrice * count;
	}

}
