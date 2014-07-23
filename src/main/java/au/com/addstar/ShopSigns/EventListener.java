package au.com.addstar.ShopSigns;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener
{
	private SignManager mManager;
	
	public EventListener(SignManager manager)
	{
		mManager = manager;
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onSignCreate(SignChangeEvent event)
	{
		mManager.initializeSign(event);
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onSignClick(PlayerInteractEvent event)
	{
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		
		if(event.getClickedBlock() == null)
			return;
		
		BlockState rawState = event.getClickedBlock().getState();
		if(rawState instanceof Sign)
		{
			InteractiveSign sign = mManager.parseSign((Sign)rawState);
			if(sign != null)
			{
				if(event.getAction() == Action.LEFT_CLICK_BLOCK)
					sign.onLeftClick(event.getPlayer(), event.getItem());
				else
					sign.onRightClick(event.getPlayer(), event.getItem());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onSignDestroy(BlockBreakEvent event)
	{
		if(event.getPlayer().isSneaking())
			return;
		
		BlockState rawState = event.getBlock().getState();
		if(rawState instanceof Sign)
		{
			InteractiveSign sign = mManager.parseSign((Sign)rawState);
			if(sign != null)
				event.setCancelled(true);
		}
	}
}
