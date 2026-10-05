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

import com.balugaq.summaryhelper.api.CachedRequest;
import com.balugaq.summaryhelper.core.commands.SubCommand;
import com.balugaq.summaryhelper.implementation.SummaryHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindNearestSlimefunBlockCommand extends SubCommand {
    @Override
    public @NotNull String getIdentifier() {
        return "findNearestSlimefunBlock";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Location location = sender instanceof Player player ? player.getLocation() : new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        World world = location.getWorld();
        SummaryHelper.getInstance().addRequest(new CachedRequest(sender, (timings) -> {
            Set<Location> validLocations = new HashSet<>();
            for (int x = -10; x <= 10; x++) {
                for (int y = -10; y <= 10; y++) {
                    for (int z = -10; z <= 10; z++) {
                        Location testLocation = new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z);
                        if (timings.containsKey(testLocation)) {
                            validLocations.add(testLocation);
                        }
                    }
                }
            }

            double minDistance = Double.MAX_VALUE;
            Location nearestLocation = null;
            if (validLocations.isEmpty()) {
                for (Location testLocation : timings.keySet()) {
                    World testWorld = testLocation.getWorld();
                    if (testWorld != null && testWorld.getName().equals(world.getName())) {
                        double distance = testLocation.distance(location);
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestLocation = testLocation;
                        }
                    }
                }
            } else {
                for (Location testLocation : validLocations) {
                    double distance = testLocation.distance(location);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestLocation = testLocation;
                    }
                }
            }

            if (nearestLocation == null) {
                sender.sendMessage("No Slimefun block found in the nearby area.");
                return;
            }

            if (sender instanceof Player player) {
                nearestLocation.add(0.5, 0.5, 0.5);
                if (!world.getWorldBorder().isInside(nearestLocation)) {
                    sender.sendMessage("Location is outside the world border.");
                    return;
                }
                player.teleport(nearestLocation);
                sender.sendMessage("Teleported to the nearest Slimefun block.");
            } else {
                sender.sendMessage("The nearest Slimefun block is at " + world.getName() + ";" + nearestLocation.getBlockX() + ":" + nearestLocation.getBlockY() + ":" + nearestLocation.getBlockZ());
            }
        }));
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
