package com.balugaq.summaryhelper.core.commands.list;

import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.core.commands.SubCommand;
import com.balugaq.summaryhelper.implementation.SummaryHelper;
import com.balugaq.summaryhelper.utils.HoverUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class GetTimingsCommand extends SubCommand {
    @Override
    public @NotNull String getIdentifier() {
        return "getTimings";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        SummaryHelper.getInstance().addRequest(new CachedRequest(sender, (timings) -> {

            Stream<Map.Entry<Location, Long>> sorted = timings.entrySet().stream()
                    .sorted(Map.Entry.<Location, Long>comparingByValue().reversed());
            List<Map.Entry<Location, Long>> top20 = sorted.limit(20).toList();

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Location, Long> entry : top20) {
                Location location = entry.getKey();
                long time = entry.getValue();
                SlimefunItem sfItem = StorageCacheUtils.getSfItem(location);
                assert sfItem != null;
                String name = sfItem.getItemName();
                sb
                        .append(ChatColor.YELLOW)
                        .append(location.getWorld().getName())
                        .append(";")
                        .append(location.getBlockX())
                        .append(":")
                        .append(location.getBlockY())
                        .append(":")
                        .append(location.getBlockZ())
                        .append(ChatColor.GRAY)
                        .append(" - ")
                        .append(name)
                        .append(ChatColor.GRAY)
                        .append(" - ")
                        .append(ChatColor.AQUA)
                        .append(NumberUtils.getAsMillis(time))
                        .append("\n");
            }

            HoverUtil.send(sender, "Top 20 block timings (Hover for details)", sb.toString());
        }));
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
