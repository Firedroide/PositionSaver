package me.firedroide.plugins.PositionSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WorldGroup {
	
	private List<World> worlds;
	private Map<String, Location> locations;
	private File saveFile;
	private String groupName;
	
	public WorldGroup(String name, ConfigurationSection section, PositionSaver plugin) {
		groupName = name;
		saveFile = new File(PositionSaver.getWorldFolder(), name + ".yml");
		worlds = new ArrayList<World>();
		locations = new HashMap<String, Location>();
		
		if (saveFile.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			for (String key : config.getKeys(false)) {
				Location loc = locationFromSection(config.getConfigurationSection(key));
				if (loc != null) locations.put(key, loc);
			}
		}
		
		List<String> worldNames = section.getStringList("worlds");
		if (worldNames == null) {
			plugin.getLogger().warning("Group " + name + " did not have a valid worlds list.");
			return;
		}
		
		for (String worldName : worldNames) {
			World w = Bukkit.getWorld(worldName);
			if (w == null) {
				plugin.getLogger().warning("World " + worldName + " in group " + name + " did not match any loaded world.");
			} else {
				worlds.add(w);
			}
		}
	}
	
	public String getName() {
		return groupName;
	}
	
	public List<World> getWorlds() {
		return worlds;
	}
	
	public void setLocation(Player p, Location loc) {
		locations.put(p.getName(), loc);
	}
	
	public Location getLocation(Player p) {
		return locations.get(p.getName());
	}
	
	public void save() {
		YamlConfiguration config = new YamlConfiguration();
		
		for (Entry<String, Location> entry : locations.entrySet()) {
			config.createSection(entry.getKey());
			saveToSection(entry.getValue(), config.getConfigurationSection(entry.getKey()));
		}
		
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveToSection(Location loc, ConfigurationSection section) {
		section.set("world", loc.getWorld().getName());
		section.set("x", loc.getX());
		section.set("y", loc.getY());
		section.set("z", loc.getZ());
		section.set("yaw", (double) loc.getYaw());
		section.set("pitch", (double) loc.getPitch());
	}
	
	private Location locationFromSection(ConfigurationSection section) {
		if (section == null || section.getKeys(false).isEmpty()) return null;
		String w = section.getString("world", groupName);
		World world = Bukkit.getWorld(w);
		if (world == null) return null;
		
		double x = section.getDouble("x", 0D);
		double y = section.getDouble("y", 255D);
		double z = section.getDouble("z", 0D);
		float yaw = (float) section.getDouble("yaw", 0D);
		float pitch = (float) section.getDouble("pitch", 0D);
		return new Location(world, x, y, z, yaw, pitch);
	}
}
