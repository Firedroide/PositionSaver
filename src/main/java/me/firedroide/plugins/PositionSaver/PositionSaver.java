package me.firedroide.plugins.PositionSaver;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PositionSaver extends JavaPlugin {
	
	private static File worldDir;
	private TeleportationListener tl;
	
	private ConfigurationSection worldGroups;
	private WorldManager worldManager;
	private int saveDelay;
	private boolean saveOnLogout;
	private boolean teleportCommands;
	
	@Override
	public void onEnable() {
		if (this.getConfig().getKeys(false).isEmpty()) {
			this.saveDefaultConfig();
		}
		
		worldDir = new File(this.getDataFolder(), "groups");
		if (!worldDir.exists()) worldDir.mkdirs();
		
		worldGroups = this.getConfig().getConfigurationSection("worldGroups");
		if (worldGroups != null) {
			worldManager = new WorldManager(worldGroups, this);
		} else {
			worldManager = new WorldManager();
		}
		
		saveDelay = this.getConfig().getInt("saveDelay", 12000);
		saveOnLogout = this.getConfig().getBoolean("saveOnLogout", false);
		teleportCommands = this.getConfig().getBoolean("teleportCommands", false);
		
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
		worldManager.saveGroups();
		this.getLogger().info("Positions saved successfully.");
	}
	
	public static File getWorldFolder() {
		return worldDir;
	}
	
	public WorldManager getWorldManager() {
		return worldManager;
	}
	
	public Location getLastLocation(Player p, World w) {
		return worldManager.getLastLocation(p, w);
	}
	
	public void setLastPosition(Player p, Location loc) {
		worldManager.setLastLocation(p, loc);
	}
	
	public boolean getSaveOnLogout() {
		return saveOnLogout;
	}
	
	public boolean getChangeTeleportCommands() {
		return teleportCommands;
	}
}
