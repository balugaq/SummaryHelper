package com.balugaq.summaryhelper.api;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.function.Consumer;

@Getter
public class CachedRequest {
    private final CommandSender sender;
    private final long timestamp;
    private final Consumer<Map<Location, Long>> callback;

    public CachedRequest(CommandSender sender, Consumer<Map<Location, Long>> callback) {
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
        this.callback = callback;
    }
}
