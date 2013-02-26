package me.firedroide.plugins.PositionSaver;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class Position {
	public double x;
	public double y;
	public double z;
	public double pitch;
	public double yaw;
	
	public Position() {
		this(0D, 0D, 0D);
	}
	
	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = 0D;
		this.yaw = 0D;
	}
	
	public Position(double x, double y, double z, double pitch, double yaw) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public static Position fromLocation(Location l) {
		return new Position(l.getX(), l.getY(), l.getZ(),
				(double) l.getPitch(), (double) l.getYaw());
	}
	
	public static Position fromSection(ConfigurationSection section) {
		if (section == null || section.getKeys(false).isEmpty()) return null;
		double x = section.getDouble("x", 0D);
		double y = section.getDouble("y", 255D);
		double z = section.getDouble("z", 0D);
		double pitch = section.getDouble("pitch", 0D);
		double yaw = section.getDouble("yaw", 0D);
		return new Position(x, y, z, pitch, yaw);
	}
	
	public Location toLocation(World w) {
		return new Location(w, x, y, z, (float) yaw, (float) pitch);
	}
	
	public void saveToSection(ConfigurationSection section) {
		section.set("x", x);
		section.set("y", y);
		section.set("z", z);
		section.set("pitch", pitch);
		section.set("yaw", yaw);
	}
}
