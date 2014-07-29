package au.com.addstar.ShopSigns.log;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.maxgamer.QuickShop.Shop.Shop;
import org.maxgamer.QuickShop.Shop.ShopPurchaseEvent;

public class QuickshopListener implements Listener
{
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onQuickShopPurchase(ShopPurchaseEvent event)
	{
		Shop shop = event.getShop();
		
		TradeLog.log(event.getPlayer(), shop.getOwner(), "QS", "BUY", shop.getPrice() * event.getAmount(), event.getAmount(), shop.getDataName(), null, shop.getLocation());
	}
}
