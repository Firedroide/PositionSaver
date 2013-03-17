package me.firedroide.plugins.PositionSaver;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TeleportationListener implements Listener {
	
	PositionSaver ps;
	
	public TeleportationListener(PositionSaver plugin) {
		ps = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		
		if (e.getFrom().getWorld().equals(e.getTo().getWorld())) return;
		if (e.getCause().equals(TeleportCause.NETHER_PORTAL)
				|| e.getCause().equals(TeleportCause.END_PORTAL)) return;
		
		ps.setLastPosition(e.getPlayer(), e.getFrom());
		
		if (e.getCause().equals(TeleportCause.COMMAND)
				&& !ps.getChangeTeleportCommands()) return;
		if (isIgnored(e.getPlayer(), e.getFrom().getWorld().getName())) {
			return;
		}
		
		Location loc = ps.getLastLocation(e.getPlayer(), e.getTo().getWorld());
		
		if (loc != null) {
			e.setTo(loc);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (!ps.getSaveOnLogout()) return;
		if (isIgnored(e.getPlayer(), e.getPlayer().getWorld().getName())) return;
		ps.setLastPosition(e.getPlayer(), e.getPlayer().getLocation());
	}
	
	private boolean isIgnored(Player p, String group) {
		boolean i = p.isPermissionSet("positionsaver.ignored")
				&& p.hasPermission("positionsaver.ignored");
		boolean w = p.isPermissionSet("positionsaver.ignored." + group)
				&& p.hasPermission("positionsaver.ignored." + group);
		return (i || w);
	}
}
