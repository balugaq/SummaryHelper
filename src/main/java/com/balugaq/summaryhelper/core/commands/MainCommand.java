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
package com.balugaq.summaryhelper.core.commands;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MainCommand implements TabExecutor {
    private final Plugin plugin;
    private final List<SubCommand> subCommands;

    public MainCommand(Plugin plugin, List<SubCommand> subCommands) {
        this.plugin = plugin;
        this.subCommands = subCommands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColors.color("&cYou don't have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColors.color("&cUsage: /sh <subcommand>"));
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getIdentifier().equalsIgnoreCase(args[0])) {
                if (subCommand.onCommand(sender, command, label, args)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            return null;
        }
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (SubCommand subCommand : subCommands) {
                completions.add(subCommand.getIdentifier());
            }

            return completions;
        }

        for (SubCommand subCommand : subCommands) {
            completions.addAll(subCommand.onTabComplete(sender, command, label, args));
        }

        return completions;
    }
}
