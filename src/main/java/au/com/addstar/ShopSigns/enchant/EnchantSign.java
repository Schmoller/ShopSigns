package au.com.addstar.ShopSigns.enchant;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.ShopSigns.InteractiveSign;
import au.com.addstar.ShopSigns.ShopSignsPlugin;

public class EnchantSign extends InteractiveSign
{
	private Enchantment mEnchant;
	private double mPrice;
	private int mMaxLevel;
	
	public EnchantSign(Enchantment enchant, double price, int level)
	{
		mEnchant = enchant;
		mPrice = price;
		mMaxLevel = level;
	}
	
	@Override
	public void onLeftClick( Player player, ItemStack item )
	{
	}

	@Override
	public void onRightClick( Player player, ItemStack item )
	{
		if(!player.hasPermission("shopsigns.enchant.use"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission to enchant items");
			return;
		}
		
		if(item == null)
		{
			player.sendMessage(ChatColor.GOLD + "Please hold the item you wish to enchant in your hand, then right click this sign again to enchant.");
			return;
		}
		
		if(!mEnchant.canEnchantItem(item))
		{
			player.sendMessage(ChatColor.GOLD + "This item cannot accept this enchantment");
			return;
		}
		
		// Check for conflicts
		for(Enchantment enchant : item.getEnchantments().keySet())
		{
			if(mEnchant.conflictsWith(enchant) && !mEnchant.equals(enchant))
			{
				player.sendMessage(ChatColor.GOLD + "This item cannot accept this enchantment. It will conflict with another enchantment.");
				return;
			}
		}
		
		Economy econ = ShopSignsPlugin.getEconomy();
		
		int current = item.getEnchantmentLevel(mEnchant);
		double cost = (current + 1) * mPrice;
		
		if(player.hasPermission("shopsigns.enchant.free"))
			cost = 0;
		
		if(current >= mMaxLevel)
		{
			player.sendMessage(ChatColor.GOLD + "This item cannot be enchanted. The enchantment is at max level.");
			return;
		}
		
		// Print overview and exit
		if(!player.isSneaking())
		{
			player.sendMessage(ChatColor.GOLD + "The cost of this enchantment is " + econ.format(cost));
			if(current != 0)
				player.sendMessage(ChatColor.GOLD + "Your item will go from level " + current + " to level " + (current + 1));
			
			player.sendMessage(ChatColor.GOLD + "Shift + Right click this sign to purchase the enchantment");
			return;
		}
		
		if(!econ.has(player, cost))
		{
			player.sendMessage(ChatColor.RED + "You have insufficient funds to purchase this enchantment");
			return;
		}
		
		econ.withdrawPlayer(player, cost);
		
		item.addUnsafeEnchantment(mEnchant, current+1);
		player.sendMessage(ChatColor.GREEN + "Successfully enchanted your item");
		player.sendMessage(ChatColor.GREEN + econ.format(cost) + " has been taken from your balance.");
	}

}
