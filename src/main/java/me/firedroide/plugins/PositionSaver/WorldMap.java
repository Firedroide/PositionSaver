package me.firedroide.plugins.PositionSaver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WorldMap {
	
	Map<String, Position> positions;
	File saveFile;
	
	public WorldMap(World w, PositionSaver plugin) {
		positions = new HashMap<String, Position>();
		
		saveFile = new File(plugin.getWorldFolder(), w.getName() + ".yml");
		if (saveFile.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(saveFile);
			for (String key : config.getKeys(false)) {
				Position pos = Position.fromSection(config.getConfigurationSection(key));
				if (pos != null) positions.put(key, pos);
			}
		}
	}
	
	public Location getLocation(Player p, World w) {
		Position pos = positions.get(p.getName());
		if (pos == null) return null;
		return pos.toLocation(w);
	}
	
	public void setLocation(Player p, Location l) {
		positions.put(p.getName(), Position.fromLocation(l));
	}
	
	public void save() {
		YamlConfiguration config = new YamlConfiguration();
		
		for (Entry<String, Position> entry : positions.entrySet()) {
			config.createSection(entry.getKey());
			entry.getValue().saveToSection(config.getConfigurationSection(entry.getKey()));
		}
		
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
