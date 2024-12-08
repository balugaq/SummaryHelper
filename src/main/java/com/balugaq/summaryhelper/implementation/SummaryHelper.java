package com.balugaq.summaryhelper.implementation;


import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.core.commands.MainCommand;
import com.balugaq.summaryhelper.core.commands.list.getTimingsCommand;
import com.balugaq.summaryhelper.core.listeners.SlimefunTickDoneListener;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SummaryHelper extends JavaPlugin implements SlimefunAddon {
    private static SummaryHelper instance;
    private final Queue<CachedRequest> requests = new LinkedList<>();

    public static SummaryHelper getInstance() {
        return instance;
    }
    public CachedRequest pollRequest() {
        return requests.poll();
    }

    public void addRequest(CachedRequest request) {
        requests.add(request);
    }

    public int getRequestCount() {
        return requests.size();
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("Loading SummaryHelper...");

        getLogger().info("Loading commands...");
        PluginCommand command = this.getCommand("summaryhelper");
        if (command != null) {
            command.setExecutor(new MainCommand(this, List.of(
                    new getTimingsCommand()
            )));
        } else {
            getLogger().warning("Failed to register command 'summaryhelper'.");
        }

        getLogger().info("Loading listeners...");
        new SlimefunTickDoneListener();

        getLogger().info("SummaryHelper has been enabled.");
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    @Override
    public void onDisable() {
        getLogger().info("SummaryHelper has been disabled.");
    }

    @Override
    @NotNull
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/balugaq/SummaryHelper/issues";
    }
}