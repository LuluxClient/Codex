package main.java.cx.ajneb97.libs.worldguard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldGuardAPIRegionEnterEvent extends Event{

	private Player player;
	private String region;
	private static final HandlerList handlers = new HandlerList();
	
	public WorldGuardAPIRegionEnterEvent(Player player,String region){
		this.player = player;
		this.region = region;
	}	
	
	public Player getPlayer() {
		return player;
	}

	public String getRegion() {
		return region;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
