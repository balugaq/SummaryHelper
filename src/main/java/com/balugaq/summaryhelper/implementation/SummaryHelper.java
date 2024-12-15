package com.balugaq.summaryhelper.implementation;


import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.core.commands.MainCommand;
import com.balugaq.summaryhelper.core.commands.list.FindNearestSlimefunBlockCommand;
import com.balugaq.summaryhelper.core.commands.list.GetTimingsCommand;
import com.balugaq.summaryhelper.core.commands.list.TpChunkCommand;
import com.balugaq.summaryhelper.core.commands.list.TpHighestLagBlockCommand;
import com.balugaq.summaryhelper.core.listeners.SlimefunTickDoneListener;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
public class SummaryHelper extends JavaPlugin implements SlimefunAddon {
    private static SummaryHelper instance;
    private final Queue<CachedRequest> requests = new LinkedList<>();

    public static SummaryHelper getInstance() {
        return instance;
    }

    public @Nullable CachedRequest pollRequest() {
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
                    new GetTimingsCommand(),
                    new FindNearestSlimefunBlockCommand(),
                    new TpChunkCommand(),
                    new TpHighestLagBlockCommand()
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