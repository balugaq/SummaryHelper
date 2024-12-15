package com.balugaq.summaryhelper.core.commands.list;

import com.balugaq.summaryhelper.core.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class TpChunkCommand extends SubCommand {
    @Override
    public @NotNull String getIdentifier() {
        return "tpChunk";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("Usage: /sh tpChunk <chunkX> <chunkZ> [world]");
            return true;
        }

        int chunkX, chunkZ;
        try {
            chunkX = Integer.parseInt(args[1]);
            chunkZ = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid chunk coordinates.");
            return true;
        }

        String worldName = args.length > 3 ? args[3] : null;

        World world = worldName == null ? player.getWorld() : Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage("Invalid world name.");
            return true;
        }

        Location location = new Location(world, chunkX * 16, player.getY(), chunkZ * 16, player.getLocation().getYaw(), player.getLocation().getPitch());
        if (!world.getWorldBorder().isInside(location)) {
            sender.sendMessage("Chunk is outside the world border.");
            return true;
        }
        player.teleport(location);
        sender.sendMessage("Teleported to chunk " + chunkX + " " + chunkZ + " in " + world.getName());
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (getIdentifier().equalsIgnoreCase(args[0])) {
                if (args.length == 4) {
                    return Bukkit.getWorlds().stream().map(World::getName).toList();
                }
            }
        }

        return new ArrayList<>();
    }
}
