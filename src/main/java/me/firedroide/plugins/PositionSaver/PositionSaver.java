package me.firedroide.plugins.PositionSaver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PositionSaver extends JavaPlugin {
	
	private File worldDir;
	private TeleportationListener tl;
	private Map<String, WorldMap> wm;
	
	private List<String> allowedWorlds;
	private boolean allowAll;
	private int saveDelay;
	private boolean saveOnLogout;
	private boolean teleportCommands;
	
	@Override
	public void onEnable() {
		if (this.getConfig().getKeys(false).isEmpty()) {
			this.saveDefaultConfig();
		}
		
		worldDir = new File(this.getDataFolder(), "worlds");
		if (!worldDir.exists()) worldDir.mkdirs();
		
		allowedWorlds = this.getConfig().getStringList("allowedWorlds");
		if (allowedWorlds == null) allowedWorlds = new ArrayList<String>();
		
		allowAll = this.getConfig().getBoolean("allowAll", false);
		saveDelay = this.getConfig().getInt("saveDelay", 12000);
		saveOnLogout = this.getConfig().getBoolean("saveOnLogout", false);
		teleportCommands = this.getConfig().getBoolean("teleportCommands", false);
		
		wm = new HashMap<String, WorldMap>();
		for (World w : Bukkit.getServer().getWorlds()) {
			if (isAllowedWorld(w)) {
				wm.put(w.getName(), new WorldMap(w, this));
			}
		}
		
		tl = new TeleportationListener(this);
		Bukkit.getPluginManager().registerEvents(tl, this);
		
		if (saveDelay > 0) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
				@Override
				public void run() {
					saveConfig();
				}
			}, saveDelay, saveDelay);
		}
	}
	
	@Override
	public void onDisable() {
		saveConfig();
	}
	
	@Override
	public void saveConfig() {
		this.getLogger().info("Saving last position data...");
		for (WorldMap map : wm.values()) {
			map.save();
		}
		this.getLogger().info("Positions saved successfully.");
	}
	
	public File getWorldFolder() {
		return worldDir;
	}
	
	public Map<String, WorldMap> getWorldMaps() {
		return wm;
	}
	
	public Location getLastPosition(World w, Player p) {
		if (wm.containsKey(w.getName())) {
			return wm.get(w.getName()).getLocation(p, w);
		} else {
			return null;
		}
	}
	
	public void setLastPosition(Location l, Player p) {
		if (wm.containsKey(l.getWorld().getName())) {
			wm.get(l.getWorld().getName()).setLocation(p, l);
		}
	}
	
	public boolean isAllowedWorld(World w) {
		return (allowAll || allowedWorlds.contains(w.getName()));
	}
	
	public boolean getSaveOnLogout() {
		return saveOnLogout;
	}
	
	public boolean getChangeTeleportCommands() {
		return teleportCommands;
	}
}
