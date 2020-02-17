package com.javabean.hub;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class HubCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player)sender;
			String commandSoFar = "/hub";
			if(args.length == 0){
				player.sendMessage(ChatColor.RED + "The " + commandSoFar + " command can have arguments: <disable | enable | set>.");
			}
			else if(args.length == 1){
				if(args[0].equalsIgnoreCase("disable")){
					Hub.hubEnabled = false;
					player.sendMessage(ChatColor.GREEN + "Hub disabled.");
				}
				else if(args[0].equalsIgnoreCase("enable")){
					Hub.hubEnabled = true;
					player.sendMessage(ChatColor.GREEN + "Hub enabled.");
				}
				else if(args[0].equalsIgnoreCase("set")){
					Hub.hubSpawn = new Location(player.getWorld(),
							player.getLocation().getX(),
							player.getLocation().getY(),
							player.getLocation().getZ(),
							player.getLocation().getYaw(),
							player.getLocation().getPitch());
					player.sendMessage(ChatColor.GREEN + "Hub set.");
				}
				else{
					player.sendMessage(ChatColor.RED + "The " + commandSoFar + " command can have arguments: <disable | enable | set>.");
				}
			}
			else{
				player.sendMessage(ChatColor.RED + "Too many arguments specified for " + commandSoFar + " command.");
			}
		}
		return true;
	}
}