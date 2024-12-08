package com.balugaq.summaryhelper.core.commands.list;

import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.core.commands.SubCommand;
import com.balugaq.summaryhelper.implementation.SummaryHelper;
import com.balugaq.summaryhelper.utils.HoverUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import net.guizhanss.guizhanlib.minecraft.ChatColors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class getTimingsCommand extends SubCommand {
    @Override
    public @NotNull String getIdentifier() {
        return "getTimings";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (getIdentifier().equals(args[0])) {
            SummaryHelper.getInstance().addRequest(new CachedRequest(sender, (timings) -> {
                // 获取耗时最高的二十个方块
                Stream<Map.Entry<Location, Long>> sorted = timings.entrySet().stream()
                        .sorted(Map.Entry.<Location, Long>comparingByValue().reversed());
                List<Map.Entry<Location, Long>> top20 = sorted.limit(20).toList();
                // 格式化输出
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Location, Long> entry : top20) {
                    Location location = entry.getKey();
                    long time = entry.getValue();
                    String id = StorageCacheUtils.getBlock(location).getSfId();
                    String name = SlimefunItem.getById(id).getItemName();
                    sb
                            .append(ChatColors.YELLOW)
                            .append(location.getWorld().getName())
                            .append(";")
                            .append(location.getBlockX())
                            .append(":")
                            .append(location.getBlockY())
                            .append(":")
                            .append(location.getBlockZ())
                            .append(" - ")
                            .append(id)
                            .append(" - ")
                            .append(name)
                            .append(" - ")
                            .append(NumberUtils.getAsMillis(time))
                            .append("\n");
                }

                HoverUtil.send(sender, "Top 20 block timings (Hover for details)", sb.toString());

            }));
            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
