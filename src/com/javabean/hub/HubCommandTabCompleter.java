package com.javabean.hub;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class HubCommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		LinkedList<String> options = new LinkedList<String>();
		if(sender instanceof Player){
			if(args.length == 1){
				String[] possible = {"disable", "enable", "set"};
				addIfStartsWith(options, possible, args[0]);
			}
		}
		return options;
	}
	
	private void addIfStartsWith(LinkedList<String> options, String[] possible, String arg){
		for(String option : possible){
			if(option.startsWith(arg)){
				options.add(option);
			}
		}
	}
}
