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
package com.balugaq.summaryhelper.core.commands.list;

import com.balugaq.summaryhelper.core.commands.SubCommand;
import com.balugaq.summaryhelper.implementation.SummaryHelper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskCommand extends SubCommand {
    public static final Map<Integer, BukkitTask> tasks = new HashMap<>();
    @Override
    public @NotNull String getIdentifier() {
        return "task";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Usage: /summaryhelper task <start|stop> <times>");
            return true;
        }

        String action = args[1];
        int times;
        try {
            times = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid times");
            return true;
        }
        World world = player.getWorld();
        switch (action) {
            case "start" -> {
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < times; i++) {
                            for (Entity entity : world.getEntities()) {
                                if (entity instanceof Player) {
                                    continue;
                                }
                                entity.teleport(entity.getLocation().add(0, 1, 0));
                                entity.teleport(entity.getLocation().add(0, -1, 0));
                            }
                        }
                    }
                };
                BukkitTask task = runnable.runTaskTimer(SummaryHelper.getInstance(), 1, 1);
                tasks.put(task.getTaskId(), task);
                player.sendMessage("Task started with id " + task.getTaskId());
            }
            case "stop" -> {
                int id = Integer.parseInt(args[2]);
                BukkitTask task = tasks.get(id);
                if (task == null) {
                    player.sendMessage("Task with id " + id + " not found");
                } else {
                    task.cancel();
                    tasks.remove(id);
                    player.sendMessage("Task with id " + id + " stopped");
                }
            }
            default -> {
                player.sendMessage("Invalid action");
            }
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            return List.of("start", "stop");
        }
        return new ArrayList<>();
    }
}
