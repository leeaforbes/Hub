package com.javabean.hub;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;

public class HubListener implements Listener{
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(Hub.hubEnabled){
			event.getPlayer().teleport(Hub.hubSpawn);
		}
		Hub.updateBoundStatus(event.getPlayer());
		event.setJoinMessage(ChatColor.GREEN + "" + event.getPlayer().getName() + "" + ChatColor.DARK_GREEN + " joined the server.");
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event){
		Hub.updateBoundStatus(event.getPlayer());
		event.setQuitMessage(ChatColor.RED + "" + event.getPlayer().getName() + "" + ChatColor.DARK_RED + " left the server.");
	}
	
	//priority set to low so that it will be run first - taken out
	//if another plugin also handles this event, it will be run afterwards to correct it
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(Hub.hubEnabled && Hub.playersInBoundsList.containsKey(event.getPlayer().getName())){
			event.setRespawnLocation(Hub.hubSpawn);
			event.getPlayer().teleport(Hub.hubSpawn);
		}
		Hub.updateBoundStatus(event.getPlayer());
	}
	
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event){
		//if player is not OP
		if(!event.getPlayer().isOp()){
			event.setCancelled(true);
		}
		//player not in creative mode
		else if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event){
		//if player is not OP
		if(!event.getPlayer().isOp()){
			event.setCancelled(true);
		}
		//player not in creative mode
		else if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
			event.setCancelled(true);
		}
	}
	
	//prevents the player from losing hunger
	@EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
		if(Hub.hubEnabled && event.getEntityType() == EntityType.PLAYER && Hub.playersInBoundsList.containsKey(((Player)event.getEntity()).getName())){
			event.setCancelled(true);
		}
    }
	
	//item durability will not decrease on use
	@EventHandler
	public void onPlayerItemDamage(PlayerItemDamageEvent event){
		if(Hub.hubEnabled && Hub.playersInBoundsList.containsKey(event.getPlayer().getName())){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntityType() == EntityType.PLAYER && Hub.playersInBoundsList.containsKey(((Player)event.getEntity()).getName())){
			event.setCancelled(true);
        }
    }
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if(event.getEntityType() == EntityType.PLAYER && Hub.playersInBoundsList.containsKey(((Player)event.getEntity()).getName())){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)){
			signClickEvent(event);
		}
	}
	
	private void signClickEvent(PlayerInteractEvent event){
		if(Hub.isASign(event.getClickedBlock().getType())){
			Hub.updateBoundStatus(event.getPlayer());
		}
	}
}
