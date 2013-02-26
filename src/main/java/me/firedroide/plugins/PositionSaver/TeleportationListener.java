package me.firedroide.plugins.PositionSaver;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldLoadEvent;

public class TeleportationListener implements Listener {
	
	PositionSaver ps;
	
	public TeleportationListener(PositionSaver plugin) {
		ps = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		
		if (e.getCause().equals(TeleportCause.NETHER_PORTAL)
				|| e.getCause().equals(TeleportCause.END_PORTAL)) return;
		if (e.getFrom().getWorld().equals(e.getTo().getWorld())) return;
		
		ps.setLastPosition(e.getFrom(), e.getPlayer());
		
		if (e.getCause().equals(TeleportCause.COMMAND)
				&& !ps.getChangeTeleportCommands()) return;
		if (isIgnored(e.getPlayer(), e.getFrom().getWorld().getName())) {
			return;
		}
		
		Location loc = ps.getLastPosition(e.getTo().getWorld(), e.getPlayer());
		
		if (loc != null) {
			e.setTo(loc);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (!ps.getSaveOnLogout()) return;
		if (isIgnored(e.getPlayer(), e.getPlayer().getWorld().getName())) return;
		ps.setLastPosition(e.getPlayer().getLocation(), e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoad(WorldLoadEvent e) {
		if (ps.getWorldMaps().containsKey(e.getWorld().getName())) return;
		if (ps.isAllowedWorld(e.getWorld())) {
			ps.getWorldMaps().put(e.getWorld().getName(), new WorldMap(e.getWorld(), ps));
		}
	}
	
	private boolean isIgnored(Player p, String world) {
		boolean i = p.isPermissionSet("positionsaver.ignored")
				&& p.hasPermission("positionsaver.ignored");
		boolean w = p.isPermissionSet("positionsaver.ignored." + world)
				&& p.hasPermission("positionsaver.ignored." + world);
		return (i || w);
	}
}
