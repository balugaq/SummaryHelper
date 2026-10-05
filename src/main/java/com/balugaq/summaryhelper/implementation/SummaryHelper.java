/**
 * MIT License
 *
 * Copyright (c) 2024-2026 balugaq
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.balugaq.summaryhelper.implementation;


import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.core.commands.MainCommand;
import com.balugaq.summaryhelper.core.commands.list.FindNearestSlimefunBlockCommand;
import com.balugaq.summaryhelper.core.commands.list.GetTimingsCommand;
import com.balugaq.summaryhelper.core.commands.list.ReloadCommand;
import com.balugaq.summaryhelper.core.commands.list.TaskCommand;
import com.balugaq.summaryhelper.core.commands.list.TpChunkCommand;
import com.balugaq.summaryhelper.core.commands.list.TpHighestLagBlockCommand;
import com.balugaq.summaryhelper.core.listeners.SlimefunTickDoneListener;
import com.balugaq.summaryhelper.core.managers.ConfigManager;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import net.guizhanss.guizhanlibplugin.bstats.bukkit.Metrics;
import net.guizhanss.guizhanlibplugin.bstats.charts.SimplePie;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;
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
    private final String username = "balugaq";
    private final String repo = "SummaryHelper";
    private final String branch = "master";
    private ConfigManager configManager;
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

        getLogger().info("Loading config...");
        configManager = new ConfigManager(this);

        getLogger().info("Loading commands...");
        PluginCommand command = this.getCommand("summaryhelper");
        if (command != null) {
            command.setExecutor(new MainCommand(this, List.of(
                    new GetTimingsCommand(),
                    new FindNearestSlimefunBlockCommand(),
                    new TpChunkCommand(),
                    new TpHighestLagBlockCommand(),
                    new ReloadCommand(),
                    new TaskCommand()
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

    public void tryUpdate() {
        try {
            if (configManager.isAutoUpdate() && getDescription().getVersion().startsWith("Build")) {
                GuizhanUpdater.start(this, getFile(), username, repo, branch);
            }
        } catch (NoClassDefFoundError | NullPointerException | UnsupportedClassVersionError e) {
            getLogger().info("自动更新失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}