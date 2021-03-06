package io.github.thatsmusic99.headsplus.api.events;

import io.github.thatsmusic99.headsplus.api.Head;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityHeadDropEvent extends Event implements Cancellable {

    // O
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Head head;
    private Player player;
    private EntityType entityType;
    private Location location;

    public EntityHeadDropEvent(Player killer, Head head, Location location, EntityType entityType) {
        this.player = killer;
        this.head = head;
        this.location = location;
        this.entityType = entityType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Head getHead() {
        return head;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setHead(Head skull) {
        this.head = skull;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
