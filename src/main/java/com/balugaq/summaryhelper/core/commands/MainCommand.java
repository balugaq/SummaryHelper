package com.balugaq.summaryhelper.core.commands;

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
            sender.sendMessage("You don't have permission to use this command.");
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
