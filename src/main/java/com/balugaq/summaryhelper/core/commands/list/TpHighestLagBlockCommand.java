package com.balugaq.summaryhelper.core.commands.list;

import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.core.commands.SubCommand;
import com.balugaq.summaryhelper.implementation.SummaryHelper;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class TpHighestLagBlockCommand extends SubCommand {
    @Override
    public @NotNull String getIdentifier() {
        return "tpHighestLagBlock";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }
        SummaryHelper.getInstance().addRequest(new CachedRequest(sender, (timings) -> {
            // get the highest lag block
            Map.Entry<Location, Long> highestLagBlock = timings
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            if (highestLagBlock == null) {
                sender.sendMessage(ChatColor.RED + "No lag block found.");
                return;
            }

            Location location = highestLagBlock.getKey();
            if (!location.getWorld().getWorldBorder().isInside(location)) {
                sender.sendMessage("Location is outside the world border.");
                return;
            }
            long lag = highestLagBlock.getValue();

            SlimefunItem item = StorageCacheUtils.getSfItem(location);
            assert item != null;

            String itemName = item.getItemName();
            player.sendMessage(ChatColor.GREEN + "Teleporting to the highest lag block...");
            player.sendMessage(ChatColor.GREEN + "Location: " + location.getWorld().getName() + ";" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ());
            player.sendMessage(ChatColor.GREEN + "Timings: " + NumberUtils.getAsMillis(lag));
            player.sendMessage(ChatColor.GREEN + "Block Name: " + itemName);
            location.add(0.5, 0.5, 0.5);
            player.teleport(location);
        }));
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
