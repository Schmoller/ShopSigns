package au.com.addstar.ShopSigns;

import java.util.WeakHashMap;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener
{
	private final SignManager mManager;
	private final WeakHashMap<Player, Long> mLastClick;
	
	public EventListener(SignManager manager)
	{
		mManager = manager;
		mLastClick = new WeakHashMap<>();
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onSignCreate(SignChangeEvent event)
	{
		mManager.initializeSign(event);
	}
	
	private boolean canUse(Player player)
	{
		Long time = mLastClick.get(player);
		return (time == null || System.currentTimeMillis() - time > ShopSignsPlugin.signCooldown);
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
				if(canUse(event.getPlayer()))
				{
					if(event.getAction() == Action.LEFT_CLICK_BLOCK)
						sign.onLeftClick(event.getPlayer(), event.getItem());
					else
						sign.onRightClick(event.getPlayer(), event.getItem());
					
					mLastClick.put(event.getPlayer(), System.currentTimeMillis());
				}					
				if(event.getAction() != Action.LEFT_CLICK_BLOCK || !event.getPlayer().isSneaking())
					event.setCancelled(true);
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
