package me.firedroide.plugins.PositionSaver;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WorldManager {

	private Map<String, WorldGroup> groups;

	public WorldManager() {
		groups = new HashMap<String, WorldGroup>();
	}

	public WorldManager(ConfigurationSection section, PositionSaver plugin) {
		groups = new HashMap<String, WorldGroup>();
		Map<String, Object> values = section.getValues(false);

		for (Map.Entry<String, Object> e : values.entrySet()) {
			if (e.getValue() instanceof ConfigurationSection) {
				WorldGroup wg = new WorldGroup(e.getKey(), (ConfigurationSection) e.getValue(), plugin);
				groups.put(e.getKey(), wg);
			}
		}
	}

	public void saveGroups() {
		for (WorldGroup group : groups.values()) {
			group.save();
		}
	}

	public boolean isWorldStored(World w) {
		for (WorldGroup group : groups.values()) {
			if (group.getWorlds().contains(w)) return true;
		}
		return false;
	}

	public WorldGroup getWorldGroup(String name) {
		return groups.get(name);
	}

	public WorldGroup getWorldGroup(World world) {
		for (WorldGroup group : groups.values()) {
			if (group.getWorlds().contains(world)) {
				return group;
			}
		}
		return null;
	}

	public void setLastLocation(Player p, Location loc) {
		WorldGroup wg = getWorldGroup(loc.getWorld());
		if (wg == null) return;
		wg.setLocation(p, loc);
	}

	public Location getLastLocation(Player p, World w) {
		WorldGroup wg = getWorldGroup(w);
		if (wg == null) return null;
		return wg.getLocation(p);
	}
}
