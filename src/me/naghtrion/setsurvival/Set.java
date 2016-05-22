package me.naghtrion.setsurvival;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Set extends JavaPlugin 
{
	public static ArrayList<String> delay = new ArrayList<String>();

	public void onEnable() 
	{
		saveDefaultConfig();
		getLogger().info("Plugin Iniciado");
	}

	public void onDisable() 
	{
		saveDefaultConfig();
		getLogger().info("Plugin Desativado");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		final Player p = (Player)sender;
		File location = new File(getDataFolder(), "locations.yml");
		FileConfiguration file = YamlConfiguration.loadConfiguration(location);

		if (args.length == 0) 
		{
		    if (!(sender instanceof Player)) 
		    {
		    	sender.sendMessage("O Console nao pode executar este comando!");
		        return false;
		    }
		    else if (file.get("Survival") == null) 
			{
				p.sendMessage(getConfig().getString("SemLocation").replace("&", "§"));
				return false;
			}
			World w = getServer().getWorld(file.getString("Survival.Mundo"));
			final Location l = new Location(w, file.getDouble("Survival.X"), file.getDouble("Survival.Y"), file.getDouble("Survival.Z"));
			l.setYaw((float) file.getDouble("Survival.Yaw"));
			l.setPitch((float) file.getDouble("Survival.Pitch"));
			if (!delay.contains(p)) 
			{
				delay.add(p);
				p.sendMessage(getConfig().getString("Teleporte.Aguarde").replace("&", "§").replace("@tempo", Integer.toString(getConfig().getInt("Teleporte.Delay"))));
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
				{
					public void run() 
					{
						delay.remove(p);
						p.teleport(l);
						p.sendMessage(getConfig().getString("Teleporte.Teleportado").replace("&", "§"));
					}
				}, getConfig().getInt("Teleporte.Delay") * 20L);
			}
			else 
			{
				p.sendMessage(getConfig().getString("Teleporte.Aguarde").replace("&", "§").replace("@tempo", Integer.toString(getConfig().getInt("Teleporte.Delay"))));
			}
		}
		else if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("marcar")) 
		{
		    if (!(sender instanceof Player)) 
		    {
		    	sender.sendMessage("O Console nao pode executar este comando!");
		        return false;
		    }
		    else if (!p.hasPermission("survival.set")) 
			{
				p.sendMessage(getConfig().getString("SemPermissao").replace("&", "§"));
				return false;
			}
			file.set("Survival.Mundo", p.getWorld().getName());
			file.set("Survival.X", Double.valueOf(p.getLocation().getX()));
			file.set("Survival.Y", Double.valueOf(p.getLocation().getY()));
			file.set("Survival.Z", Double.valueOf(p.getLocation().getZ()));
			file.set("Survival.Yaw", Float.valueOf(p.getLocation().getYaw()));
			file.set("Survival.Pitch", Float.valueOf(p.getLocation().getPitch()));
			try 
			{
				file.save(location);
				p.sendMessage(getConfig().getString("Location.Marcado").replace("&", "§"));
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("apagar") || args[0].equalsIgnoreCase("deletar")) 
		{
			if (!p.hasPermission("survival.del")) 
			{
				p.sendMessage(getConfig().getString("SemPermissao").replace("&", "§"));
				return false;
			}
			if (file.getString("Survival") == null) 
			{
				p.sendMessage(getConfig().getString("SemLocation").replace("&", "§"));
				return false;
			}
			else 
			{
				file.set("Survival", null);
				try 
				{
					file.save(location);
					p.sendMessage(getConfig().getString("Location.Apagado").replace("&", "§"));
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
