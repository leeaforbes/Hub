package com.javabean.hub;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Bounds {
	private Location bound1;
	private Location bound2;
	private String name;
	
	//player name, Player
	private HashMap<String, Player> playersInBounds = new HashMap<String, Player>();
	
	public Bounds(Location b1, Location b2, String n){
		setBound1(b1);
		setBound2(b2);
		setName(n);
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public Location getBound1(){
		return bound1;
	}
	
	public void setBound1(Location b1){
		bound1 = b1;
	}
	
	public Location getBound2(){
		return bound2;
	}
	
	public void setBound2(Location b2){
		bound2 = b2;
	}
	
	public void addPlayer(Player player){
		playersInBounds.put(player.getName(), player);
	}
	
	public void removePlayer(Player player){
		playersInBounds.remove(player.getName());
	}
	
	public void emptyPlayers(){
		playersInBounds.clear();
	}
	
	public void updateInBoundsPlayers(Collection<Player> onlinePlayers){
		playersInBounds.clear();
		for(Player player : onlinePlayers){
			if(isLocationInBounds(player.getLocation())){
				playersInBounds.put(player.getName(), player);
			}
		}
	}
	
	public boolean containsPlayer(Player player){
		return playersInBounds.containsKey(player.getName());
	}
	
	public boolean isLocationInBounds(Location location){
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		double b1x = bound1.getX();
		double b1y = bound1.getY();
		double b1z = bound1.getZ();
		double b2x = bound2.getX();
		double b2y = bound2.getY();
		double b2z = bound2.getZ();
		return (location.getWorld().getName().equals(bound1.getWorld().getName())
				&& ((x < b1x && x > b2x) || (x > b1x && x < b2x))
				&& ((y < b1y && y > b2y) || (y > b1y && y < b2y))
				&& ((z < b1z && z > b2z) || (z > b1z && z < b2z)));
	}
}
