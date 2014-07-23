package au.com.addstar.ShopSigns.buy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;

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
		player.sendMessage("Left click");
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		player.sendMessage("Right click");
	}

}
